package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.SystemConstants;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.apache.http.client.RedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.service.impl *
 * @since 1.0
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<TbSeckillGoods> findAll() {
        //从redis中获取商品的列表
        List<TbSeckillGoods> values = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_REDIS_BIGKEY).values();
        if(values==null || values.size()==0){
            values = new ArrayList<>();
        }
        return values;
    }

    @Override
    public TbSeckillGoods findOne(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_REDIS_BIGKEY).get(id);
    }
}
