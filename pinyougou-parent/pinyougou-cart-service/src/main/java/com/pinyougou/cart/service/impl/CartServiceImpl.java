package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service.impl *
 * @since 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> origionList, Long itemId, Integer num) {

        //1.根据itemId获取商品SKU的数据
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

        //2.获取要添加的商品的数据对应的 商家的ID
        String sellerId = tbItem.getSellerId();//商家的ID

        Cart cart = searchCartBySellerId(origionList,sellerId);
        //3.判断 要添加的商品 所属的商家的ID 在购物车列表中是否存在 如果存在
        if(cart!=null) {

              TbOrderItem orderItem = searchOrderItemById(itemId,cart.getOrderItemList());
              if(orderItem!=null) {
                  //3.1 判断 要购买的商品 是否在 明细列表 中是否存在  如果存在   数量相加

                  orderItem.setNum(orderItem.getNum()+num);
                  double v = orderItem.getNum() * orderItem.getPrice().doubleValue();
                  orderItem.setTotalFee(new BigDecimal( v));

                  //判断 如果 购买的数量为0 删除购买的商品
                  if(orderItem.getNum()<=0){
                      cart.getOrderItemList().remove(orderItem);
                  }
                  //判断 如果 没有商品了。就把商家对应的对象也删除了
                  if(cart.getOrderItemList().size()==0){
                      origionList.remove(cart);
                  }

              }else {
                  //3.2 判断 要购买的商品 是否在 明细列表 中是否存在  如果不存在  直接添加商品即可


                  List<TbOrderItem> orderItemList = cart.getOrderItemList();
                  orderItem = new TbOrderItem();
                  orderItem.setPicPath(tbItem.getImage());
                  orderItem.setItemId(tbItem.getId());
                  orderItem.setGoodsId(tbItem.getGoodsId());
                  orderItem.setTitle(tbItem.getTitle());
                  orderItem.setPrice(tbItem.getPrice());
                  orderItem.setNum(num);
                  double v = num * tbItem.getPrice().doubleValue();
                  orderItem.setTotalFee(new BigDecimal(v));
                  orderItem.setSellerId(sellerId);
                  orderItemList.add(orderItem);

              }

        }else {
            //4.判断 要添加的商品 所属的商家的ID 在购物车列表中是否存在 如果不存在
            //直接添加商品了
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> orderitemList = new ArrayList<>();//你在该商家买的商品明细列表


            TbOrderItem tborderItem= new TbOrderItem();
            tborderItem.setPicPath(tbItem.getImage());
            tborderItem.setItemId(tbItem.getId());
            tborderItem.setGoodsId(tbItem.getGoodsId());
            tborderItem.setTitle(tbItem.getTitle());
            tborderItem.setPrice(tbItem.getPrice());
            tborderItem.setNum(num);
            double v = num * tbItem.getPrice().doubleValue();
            tborderItem.setTotalFee(new BigDecimal(v));
            tborderItem.setSellerId(sellerId);
            orderitemList.add(tborderItem);

            cart.setOrderItemList(orderitemList);


            origionList.add(cart);
        }

        return origionList;
    }

    @Override
    public List<Cart> getCartListFromRedis(String username) {
        List<Cart> redis_cart_list = (List<Cart>) redisTemplate.boundHashOps("REDIS_CART_LIST").get(username);
        if(redis_cart_list==null || redis_cart_list.size()==0){
            redis_cart_list=new ArrayList<>();
        }
        return redis_cart_list;
    }


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartsListNewest) {

        redisTemplate.boundHashOps("REDIS_CART_LIST").put(username,cartsListNewest);

    }

    @Override
    public List<Cart> merge(List<Cart> cookieList, List<Cart> redisCartList) {

        for (Cart cart : cookieList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                redisCartList = addGoodsToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    private TbOrderItem searchOrderItemById(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    private Cart searchCartBySellerId(List<Cart> origionList, String sellerId) {
        for (Cart cart : origionList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }

        }
        return null;
    }
}
