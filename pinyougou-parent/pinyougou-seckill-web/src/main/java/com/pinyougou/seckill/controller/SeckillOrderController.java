package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            //判断 如果是匿名用户 表示没有登录
            if(userId.equals("anonymousUser")){
                //如果是401 表示没有登录 要登录
                return new Result(false,"401");
            }

            seckillOrderService.add(seckillId,userId);
            return new Result(true,"下单成功");
        }catch (RuntimeException e) {
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"下单失败");
        }
    }
}
