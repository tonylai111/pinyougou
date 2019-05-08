package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WxPayService;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.pay.service.impl *
 * @since 1.0
 */
@Service
public class WxPayServiceImpl implements WxPayService {
    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        try {
            //1.组合参数  Map
            Map<String,String> paramMap=new HashMap<>();
            paramMap.put("appid","wx8397f8696b538317");
            paramMap.put("mch_id","1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //paramMap.put("nonce_str") 签名 会用一个api 自动生成 添加签名
            paramMap.put("body","品优购");
            paramMap.put("out_trade_no",out_trade_no);
            paramMap.put("total_fee",total_fee);
            paramMap.put("spbill_create_ip","127.0.0.1");
            paramMap.put("notify_url","http://a31ef7db.ngrok.io/WeChatPay/WeChatPayNotify");
            paramMap.put("trade_type","NATIVE");//扫描支付
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //2.使用httclient 来模拟浏览器发送请求（调用统一下单的API）
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();//发送请求
            //3.使用httclient 来模拟浏览器接收响应  获取里面的code_url
            String content = httpClient.getContent();
            System.out.println(content);
            //4.组合成map ：金额 交易订单号 交易链接地址 返回
            Map resultMap = new HashMap();
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            resultMap.put("code_url",stringStringMap.get("code_url"));
            resultMap.put("total_fee",total_fee);
            resultMap.put("out_trade_no",out_trade_no);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            //1.组合参数  Map

            Map<String,String> paramMap=new HashMap<>();
            paramMap.put("appid","wx8397f8696b538317");
            paramMap.put("mch_id","1473426802");
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            //paramMap.put("nonce_str") 签名 会用一个api 自动生成 添加签名
            paramMap.put("out_trade_no",out_trade_no);

            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //2.使用httclient 来模拟浏览器发送请求（调用查询订单的API）
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();
            //3.使用httclient 来模拟浏览器接收响应  获取里面的code_url
            String content = httpClient.getContent();
            System.out.println(content);

            //4.组合成map ：有支付的状态字段
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            return stringStringMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
