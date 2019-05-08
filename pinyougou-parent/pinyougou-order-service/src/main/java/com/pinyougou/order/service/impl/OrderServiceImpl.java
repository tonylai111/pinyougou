package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.SystemConstants;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.order.service.impl *
 * @since 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbPayLogMapper logMapper;

    @Override
    public void add(TbOrder order) {

        //业务：1.订单的ID主键 需要生成  2.订单需要拆单

        //先获取到购买的商评所属的商家==先从redis中获取购物车的列表List<Cart>
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(SystemConstants.CART_REDIS_LIST_KEY).get(order.getUserId());

        double total = 0;

        List<String> ids = new ArrayList<>();

        for (Cart cart : cartList) {//cart 就是一个商家

            //1.插入订单表

            //1.1 生成订单的主键 雪花算法生成
            long orderId = new IdWorker(0, 0).nextId();
            ids.add(orderId+"");
            TbOrder ordernew = new TbOrder();
            ordernew.setOrderId(orderId);
            //ordernew.setPayment();//在某一个商家  购买的所有的商品的总金额

            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            double totalMoney=0;
            for (TbOrderItem orderItem : orderItemList) {
                double totalFee = orderItem.getTotalFee().doubleValue();
                totalMoney+=totalFee;

                //2.插入订单选项表
                //生成订单项的ID 雪花算法
                long orderItemId = new IdWorker(0, 0).nextId();
                orderItem.setId(orderItemId);
                orderItem.setOrderId(orderId);
                orderItem.setSellerId(cart.getSellerId());
                orderItemMapper.insert(orderItem);
            }
            ordernew.setPayment(new BigDecimal(totalMoney));


            total+=totalMoney;//元

            ordernew.setPaymentType(order.getPaymentType());
            ordernew.setPostFee("0");
            ordernew.setStatus("1");//未付款
            ordernew.setCreateTime(new Date());
            ordernew.setUpdateTime(ordernew.getCreateTime());
            ordernew.setUserId(order.getUserId());

            ordernew.setReceiverMobile(order.getReceiverMobile());
            ordernew.setReceiverAreaName(order.getReceiverAreaName());//详细地址
            ordernew.setReceiverZipCode("518000");//邮编ordernew。
            ordernew.setReceiver(order.getReceiver());//收货人
            ordernew.setSellerId(cart.getSellerId());
            orderMapper.insert(ordernew);




        }

        //添加支付记录
        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(new IdWorker(0,0).nextId()+"");
        payLog.setCreateTime(new Date());
        //0.01
        double v = total * 100;//分
        payLog.setTotalFee((long)v);
        payLog.setUserId(order.getUserId());
        payLog.setTradeState("0");//未支付
        payLog.setOrderList(ids.toString().replace("[","").replace("]",""));
        payLog.setPayType("1");//微信支付

        logMapper.insert(payLog);

        //记录存储到redis中
        redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).put(order.getUserId(),payLog);


        //清空购物车
        redisTemplate.boundHashOps(SystemConstants.CART_REDIS_LIST_KEY).delete(order.getUserId());

    }

    @Override
    public TbPayLog getTbLogByUserIdFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).get(userId);
    }

    @Override
    public void updateStatus(String transaction_id, String out_trade_no) {
        //1.更新支付日志记录
        TbPayLog payLog = logMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("1");
        payLog.setTransactionId(transaction_id);
        payLog.setPayTime(new Date());//支付的时间
        logMapper.updateByPrimaryKey(payLog);
        //2.更新订单
            //2.1 获取订单的订单号
        String orderList = payLog.getOrderList();//  37,38
        String[] orderids = orderList.split(",");
        for (String orderid : orderids) {
            //2.2 获取订单的对象  更新状态
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(orderid));
            tbOrder.setStatus("2");//已经付款
            tbOrder.setUpdateTime(new Date());
            tbOrder.setPaymentTime(tbOrder.getUpdateTime());
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //3.删除redis
        redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).delete(payLog.getUserId());

    }

    public static void main(String[] args) {
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        System.out.println(ids.toString().replace("[","").replace("]",""));
    }
}
