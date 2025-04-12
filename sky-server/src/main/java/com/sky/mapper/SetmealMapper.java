package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    List<SetmealVO> selectPage(@Param("setmealPageQueryDTO") SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 插入套餐
     * @param setmeal
     */
    @AutoFill(value=OperationType.INSERT)
    void insert(@Param("setmeal")Setmeal setmeal);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    Setmeal selectById(Long id);

    /**
     * 更新
     * @param setmeal
     */
    @AutoFill(value=OperationType.UPDATE)
    @Update("UPDATE setmeal SET category_id=#{setmeal.categoryId},name=#{setmeal.name},price=#{setmeal.price},description=#{setmeal.description},create_time=#{setmeal.createTime},update_time=#{setmeal.updateTime} WHERE id=#{setmeal.id}")
    void update(@Param("setmeal") Setmeal setmeal);

    /**
     * 更新套餐状态
     * @param status
     * @param id
     */
    @Update("UPDATE setmeal SET status=#{status},id=#{id}")
    void updateStatus(@Param("status") Integer status,@Param("id") Long id);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteByIds(@Param("ids") List<Long> ids);
}
