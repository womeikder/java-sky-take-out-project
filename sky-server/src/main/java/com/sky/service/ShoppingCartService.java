package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 为购物车添加商品
     * @param cartDTO
     */
    void add(ShoppingCartDTO cartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> list(Long currentId);

    /**
     * 清空购物车
     * @param currentId
     */
    void delete(Long currentId);

    /**
     * 减少一个购物车中的菜品或套餐
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
