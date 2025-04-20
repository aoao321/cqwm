package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-18-16:22
 */
public interface ShoppingCartService {
    /**
     * 用户将菜品或者套餐加入购物车中
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车数据
     * @return
     */
    List<ShoppingCart> show();

    /**
     * 删除套餐或者菜品
     * @param shoppingCartDTO
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void clean();

}
