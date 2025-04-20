package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-18-16:29
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 返回购物车所有商品
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 菜品数量+1
     * @param cart
     */
    @Update("UPDATE shopping_cart SET number=#{number} WHERE id=#{id}")
    void updateNumber(ShoppingCart cart);

    /**
     * 加入购物车
     * @param shoppingCart
     */
    @Insert("INSERT INTO shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) VALUES " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id查询购物车
     * @param userId
     * @return
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id=#{userId}")
    List<ShoppingCart> selectByUserId(Long userId);

    /**
     * 删除商品
     * @param cart
     */
    void delete(ShoppingCart cart);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id=#{userId}")
    void deleteAll(Long userId);
}
