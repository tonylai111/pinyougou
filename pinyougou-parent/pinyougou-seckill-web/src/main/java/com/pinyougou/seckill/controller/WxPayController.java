package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WxPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/pay")
public class WxPayController {

    @Reference
    private WxPayService wxPayService;

    @Reference
    private OrderService orderService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative(){

        //从redis中获取当前登录的用户的支付记录对象

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

       //从 redis中获取秒杀订单 获取订单中的金额 和 交易订单号
       TbSeckillOrder order =  seckillOrderService.getSeckillOrderByUserId(userId);


        if(order!=null) {
            //1.生成交易订单号
            String out_trade_no = order.getId()+"";//订单号
            //2.支付的商品的金额
            String total_fee = (long)(order.getMoney().doubleValue()*100)+"";
            //3.调用服务 发送请求获取code_url
            return wxPayService.createNative(out_trade_no, total_fee);
        }else{
            return null;
        }
    }

    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        Result result = new Result(false,"失败");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //调用服务的方法 ---》发送请求给微信支付系统 查询状态
           Map<String,String>  resultMap = wxPayService.queryStatus(out_trade_no);
            if("SUCCESS".equals(resultMap.get("trade_state"))){
               result = new Result(true,"支付成功");
                //从redis中获取登录用户的订单，将这个订单到保存mysql中   更新支付时间  更新支付的状态  获取微信返回的流水 更新到数据库中

                seckillOrderService.saveToMysql(userId,resultMap.get("transaction_id"));

                return new Result(true,"成功");
           }
           if(resultMap.get("trade_state").equals("NOTPAY")){
                return new Result(false,"501");//501 表示没支付
           }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

    }
}
