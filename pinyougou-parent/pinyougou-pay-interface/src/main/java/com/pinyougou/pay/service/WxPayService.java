package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.pay.service *
 * @since 1.0
 */
public interface WxPayService {
    /**
     *
     * @param out_trade_no  外部生成的交易订单号
     * @param total_fee  交易的金额
     * @return
     */
    Map createNative(String out_trade_no, String total_fee);


    /**
     * 查询交易订单的支付的状态
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryStatus(String out_trade_no);

}
