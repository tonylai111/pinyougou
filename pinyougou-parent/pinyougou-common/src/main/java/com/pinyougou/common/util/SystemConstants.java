package com.pinyougou.common.util;

import java.io.Serializable;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.common.util *
 * @since 1.0
 */
public class SystemConstants implements Serializable{

    //用于redis缓存 广告缓存
    public static final String REDIS_CONTENT_KEY="REDIS_CONTENT_ContentServiceImpl_";

    public static final String CART_REDIS_LIST_KEY="REDIS_CART_LIST";

    /**
     * 秒杀商品的KEY
     */
    public static final  String  SEC_KILL_GOODS_REDIS_BIGKEY="seckillGoods";

    /**
     * 秒杀下单的订单KEY
     */

    public static final  String  SEC_KILL_ORDER_REDIS_BIGKEY="seckillOrder";


    public static final String SEC_KILL_GOODS_PREFIX="SEC_KILL_GOODS_ID_";



}
