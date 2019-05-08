package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import com.pinyougou.group.Cart;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = {"http://localhost:9105","http://localhost:9108"},allowCredentials = "true")
public class CartController {


    @Reference
    private CartService cartService;

//    @CrossOrigin
    //@CrossOrigin(origins = {"http://localhost:9105","http://localhost:9108"},allowCredentials = "true")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){
        System.out.println("hehehehehheheheh");
        try {
            //服务端 允许 指定的域（系统）可以访问资源
            //response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");//统一指定的域访问我的服务器资源
            //response.setHeader("Access-Control-Allow-Credentials", "true");//同意客户端携带cookie

            //new ArrayList<>();

            //1.先要获取用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("username>>"+username);//anonymousUser
            if("anonymousUser".equals(username)) {
                //2.判断用户是否存在 ，如果不存在 没登录  操作cookie
                System.out.println("没登录");

                //2.1 先从cookie中获取已有的购物车的列表数据

                String cartList = CookieUtil.getCookieValue(request, "cartList", true);
                List<Cart> cookieList = new ArrayList<>();
                if(StringUtils.isNotBlank(cartList)) {
                   cookieList = JSON.parseArray(cartList, Cart.class);
                }
                //2.2 向已有的购物车列表 中 添加商品  返回一个最新的购物车的列表（方法）

                List<Cart> cartsListNewest=cartService.addGoodsToCartList(cookieList,itemId,num);

                //2.3 将最新的购物车列表数据 重新写入到cookie中
                CookieUtil.setCookie(request,response,"cartList", JSON.toJSONString(cartsListNewest),7*24*3600,true);

            }else {
                //3.否则就是 登录  操作redis
                //3.1 先从redis中获取已有的购物车的列表数据
                List<Cart> cartListFromRedis=cartService.getCartListFromRedis(username);
                //3.2 向已有的购物车列表 中 添加商品  返回一个最新的购物车的列表（方法）
                List<Cart> cartsListNewest=cartService.addGoodsToCartList(cartListFromRedis,itemId,num);
                //3.3 将最新的购物车列表数据 重新写入到redis中
                cartService.saveCartListToRedis(username,cartsListNewest);//一个用户就是一个购物车  hash   bigkey  field:用户的ID  value:List<cart>

                System.out.println("已经登录");
            }
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if("anonymousUser".equals(username)) {
            //没有登录
            String cartList = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cookieList = new ArrayList<>();
            if(StringUtils.isNotBlank(cartList)) {
                cookieList = JSON.parseArray(cartList, Cart.class);
            }
            return cookieList;
        }else{
            //已经登录
            List<Cart> cartListFromRedis=cartService.getCartListFromRedis(username);

            //合并购物车数据

            //1.获取cookie中的购物车数据
            String cartList = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cookieList = new ArrayList<>();
            if(StringUtils.isNotBlank(cartList)) {
                cookieList = JSON.parseArray(cartList, Cart.class);
            }
            //2.获取redis中的购物车数据

            //3.合并购物车  返回一个最新的购物车列表
            List<Cart> mgerCartListnew = cartService.merge(cookieList,cartListFromRedis);
            //4.最新的购物车列表 写入redis中
            cartService.saveCartListToRedis(username,mgerCartListnew);
            //5.cookie 清空
            CookieUtil.deleteCookie(request,response,"cartList");
            //6.最新的购物车数据返回
            return mgerCartListnew;
        }



    }



}
