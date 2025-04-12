package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-06-18:04
 */
public interface SetmealService {

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增菜品
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 通过id查询套餐信息
     * @param id
     * @return
     */
    SetmealVO getById(Long id);

    /**
     * 修改套餐内容
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 起售或停用套餐
     * @param status
     * @param id
     */
    void starOrStop(Integer status, Long id);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
