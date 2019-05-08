package com.pinyougou.cart.service;

import com.pinyougou.group.Cart;

import java.util.List; /**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service *
 * @since 1.0
 */
public interface CartService {
    /**
     *
     * @param origionList  原来的购物车列表
     * @param itemId  要添加的商品的ID
     * @param num  要添加的商品的购买数量
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> origionList, Long itemId, Integer num);

    /**
     * 获取用户的购物车列表
     * @param username
     * @return
     */
    List<Cart> getCartListFromRedis(String username);

    /**
     * 存储用户的购物车的列表
     * @param username
     * @param cartsListNewest
     */
    void saveCartListToRedis(String username, List<Cart> cartsListNewest);


    /**
     *合并
     * @param cookieList cookie中的购物车
     * @param redisCartList redis中的购物车
     * @return
     */
    List<Cart> merge(List<Cart> cookieList, List<Cart> redisCartList);
}
