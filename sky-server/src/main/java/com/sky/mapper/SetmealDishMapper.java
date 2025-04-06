package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

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

}
