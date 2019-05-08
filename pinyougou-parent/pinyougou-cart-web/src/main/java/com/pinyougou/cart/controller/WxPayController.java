package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WxPayService;
import com.pinyougou.pojo.TbPayLog;
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

    @RequestMapping("/createNative")
    public Map createNative(){

        //从redis中获取当前登录的用户的支付记录对象

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        TbPayLog tbPayLog = orderService.getTbLogByUserIdFromRedis(userId);

        if(tbPayLog!=null) {
            //1.生成交易订单号
            String out_trade_no = tbPayLog.getOutTradeNo();
            //2.支付的商品的金额
            String total_fee = tbPayLog.getTotalFee() + "";
            //3.调用服务 发送请求获取code_url
            return wxPayService.createNative(out_trade_no, total_fee);
        }else{
            return null;
        }
    }

    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        Result result = new Result(false,"失败");
        try {
            //周期性的发送请求 获取支付的状态
            int count=0;
            while(true){

                //调用服务的方法 ---》发送请求给微信支付系统 查询状态
               Map<String,String>  resultMap = wxPayService.queryStatus(out_trade_no);
               Thread.sleep(3000);
                count++;
               //如果5分钟 超时 返回超时
                if(count>=100){

                    result = new Result(false,"超时");
                    break;
                }

                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    //1.更新支付日志记录
                    //2.更新支付日志记录对应的订单的状态信息
                    //3.删除redis中的记录
                   orderService.updateStatus(resultMap.get("transaction_id"),out_trade_no);

                   result = new Result(true,"支付成功");
                   break;
               }

            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }

    }
}
