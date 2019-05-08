package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.SystemConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.service.impl *
 * @since 1.0
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    //在redis中下一个预订单

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private TbSeckillOrderMapper orderMapper;

    @Override
    public void add(Long seckillId, String userId) {
        //1.先根据秒杀商品的ID 从REIDS获取秒杀商品的数据
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_REDIS_BIGKEY).get(seckillId);


        Object o = redisTemplate.boundListOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillId).rightPop();
        if(o==null){
            throw new RuntimeException("商品售罄");
        }
     /*   if(seckillGoods==null || seckillGoods.getStockCount()<=0){
            //2.判断 库存是否足够 ，如果商品 不存在 或者 库存不足  提示 ：商品已经售罄
             throw new RuntimeException("商品售罄");
        }*/
        //3.判断 库存是否足够 ，库存大于0

        //4.库存 -1
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);

        if(seckillGoods.getStockCount()<=0){
            //5.判断 如果库存是0  数据更新到数据库中  删除redis中的商品
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);

            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_REDIS_BIGKEY).delete(seckillId);
        }


        //6.秒杀下单（下在redis中）
        TbSeckillOrder order = new TbSeckillOrder();
        order.setId(new IdWorker(0,1).nextId());
        order.setSeckillId(seckillId);
        order.setMoney(seckillGoods.getCostPrice());
        order.setUserId(userId);
        order.setSellerId(seckillGoods.getSellerId());
        order.setCreateTime(new Date());
        order.setStatus("0");//表示未支付
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_REDIS_BIGKEY).put(userId,order);
    }

    @Override
    public TbSeckillOrder getSeckillOrderByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_REDIS_BIGKEY).get(userId);
    }

    @Override
    public void saveToMysql(String userId, String transaction_id) {
        //1.根据用户的ID 获取redis中的订单
        TbSeckillOrder orderFromRedis = getSeckillOrderByUserId(userId);
        //2.更新数据（支付的状态  时间,流水）
        orderFromRedis.setStatus("1");//已经支付
        orderFromRedis.setPayTime(new Date());
        orderFromRedis.setTransactionId(transaction_id);
        //3.更新到数据库中
        orderMapper.insert(orderFromRedis);
        //4.redis中的订单要删除
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_REDIS_BIGKEY).delete(userId);

    }
}
