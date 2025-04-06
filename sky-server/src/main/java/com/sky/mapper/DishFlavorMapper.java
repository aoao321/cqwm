package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-05-20:12
 */
@Mapper
public interface DishFlavorMapper {
    /**
     * 插入菜品口味
     * @param flavors
     */
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);

    /**
     * 删除菜品关联的口味
     * @param ids
     */
    void deleteByDishIds(@Param("ids") List<Long> ids);

    /**
     * 通过dishId获取口味
     * @param id
     * @return
     */
    @Select("SELECT * FROM dish_flavor WHERE id=#{id}")
    List<DishFlavor> selectByDishId(Long id);
}
