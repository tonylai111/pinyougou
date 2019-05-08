package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.order.service *
 * @since 1.0
 */
public interface OrderService {
    void add(TbOrder order);


    /**
     * 获取用户的支付记录
     * @param userId
     * @return
     */
    TbPayLog getTbLogByUserIdFromRedis(String userId);


    void updateStatus(String transaction_id, String out_trade_no);

}
