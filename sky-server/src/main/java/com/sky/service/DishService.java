package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-05-19:17
 */
public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void save(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 启用或者禁用
     * @param status
     * @param id
     */
    void starOrStop(int status, Long id);

    /**
     * 根据id查询菜品
     * @param id
     */
    DishVO  getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 根据分类查询菜品
     * @param categoryId
     * @return
     */
    List<DishVO> list(Long categoryId);
}
