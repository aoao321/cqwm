package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.*;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author aoao
 * @create 2025-04-18-16:22
 */
@Service
@Transactional
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //判断商品是否存在购物车中
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //菜品或者套餐如果在购物车中，则执行更新
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumber(cart);
        } else {
            //不在购物车中，执行插入
            shoppingCart.setCreateTime(LocalDateTime.now());//设置时间
            shoppingCart.setNumber(1);//设置初始数量
            //判断加入的是菜品还是套餐
            if (shoppingCartDTO.getDishId() != null) {
                //查询菜品信息
                Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
                //将信息赋值给购物车
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
            } else if (shoppingCartDTO.getSetmealId() != null) {
                //查询套餐信息
                Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
                //将信息赋给购物车
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            //插入数据库中
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> show() {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //根据userId来查询购物车中的数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectByUserId(userId);
        return shoppingCartList;
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //查询菜品或者套餐
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //设置用户id
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询
        List<ShoppingCart> cartList = shoppingCartMapper.list(shoppingCart);
        //判断减1之后的数量
        if (cartList != null && cartList.size() > 0) {
            ShoppingCart cart = cartList.get(0);
            if (cart.getNumber() - 1 > 0) { //number-1大于0更新number
                cart.setNumber(cart.getNumber() - 1);
                shoppingCartMapper.updateNumber(cart);
            } else { //等于0时删除该条购物车记录
                shoppingCartMapper.delete(cart);
            }
        }
    }

    @Override
    public void clean() {
        //获取用户id
        Long userId = BaseContext.getCurrentId();
        //通过用户id将购物车清空
        shoppingCartMapper.deleteAll(userId);
    }
}
