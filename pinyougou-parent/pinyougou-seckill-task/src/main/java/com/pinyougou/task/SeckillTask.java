package com.pinyougou.task;

import com.pinyougou.common.util.SystemConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.task *
 * @since 1.0
 */
@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //方法 就是被反复执行的任务。 隔5秒钟就执行一次。
    //cron 就是用于指定何时 执行的 表达式。
    //从0秒钟开始执行，每隔 5秒钟执行一次
    @Scheduled(cron="0/5 * * * * ? ")
    public void mysqlGoodsToRedis(){
        System.out.println(new Date());
        //从mysql中将秒杀商品的数据存储到redis中
        //1.查询所有符合条件的秒杀商品的数据


        //排除掉redis中已有的商品
        Set<Long> seckillGoods = redisTemplate.boundHashOps("seckillGoods").keys();

        TbSeckillGoodsExample exmaple = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = exmaple.createCriteria();
        criteria.andStatusEqualTo("1");//已经审核的
        //   开始时间< 当前的时间 < 结束时间
        Date date = new Date();
        criteria.andStartTimeLessThan(date);
        criteria.andEndTimeGreaterThan(date);
        //剩余库存 大于0
        criteria.andStockCountGreaterThan(0);
        if(seckillGoods!=null &&seckillGoods.size()>0) {
            List<Long> ids = new ArrayList<>();
            for (Long seckillGood : seckillGoods) {
                ids.add(seckillGood);
            }
            criteria.andIdNotIn(ids);//select * from where id not in (redis中的id)
        }

        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(exmaple);
        //2.使用redistemplate 存储数据到 redis中
        for (TbSeckillGoods good : goods) {
            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_REDIS_BIGKEY).put(good.getId(),good);

            //每一个商品都要是一个队列  队列里面的元素的长度  和剩余库存相当
            for (Integer i = 0; i < good.getStockCount(); i++) {
                redisTemplate.boundListOps(SystemConstants.SEC_KILL_GOODS_PREFIX+good.getId()).leftPush(good);//
            }
        }

    }
}
