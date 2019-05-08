package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.service *
 * @since 1.0
 */
public interface SeckillOrderService {
    /**
     * 将抢购的商品 创建一个订单
     * @param seckillId  要购买的秒杀的商品的ID
     * @param userId  用户的Id
     */
    void add(Long seckillId, String userId);

    TbSeckillOrder getSeckillOrderByUserId(String userId);

    void saveToMysql(String userId, String transaction_id);

}
