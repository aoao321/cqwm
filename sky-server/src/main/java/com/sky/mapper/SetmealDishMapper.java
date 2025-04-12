package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-06-15:56
 */
@Mapper
public interface SetmealDishMapper {
    /**
     * 通过菜品id查询套餐表关联菜品的总条数
     * @param id
     * @return
     */
    int count(Long id);

    /**
     * 批量插入菜品套餐关系表
     * @param setmealDishes
     */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 查询套餐关联的菜品
     * @param id
     * @return
     */
    List<SetmealDish> selectBySetmealId(Long id);

    /**
     * 批量删除菜品
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);
}
