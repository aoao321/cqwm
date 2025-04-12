package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("SELECT count(id) FROM dish WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入新的菜品
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    @Insert("INSERT INTO dish (name, category_id, price, image, description, create_time, update_time, create_user, update_user,status)" +
            "VALUES (#{name},#{categoryId}, #{price}, #{image}, #{description}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Dish dish);

    /**
     * 根据name分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    List<DishVO> selectDishByName(@Param("dishPageQueryDTO") DishPageQueryDTO dishPageQueryDTO);

    /**
     * 通过id查询菜品信息
     * @param id
     * @return
     */
    Dish selectById(Long id);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 更新状态
     * @param status
     * @param id
     */
    @Update("UPDATE dish SET status=#{status} WHERE id=#{id}")
    void updateStatus(@Param("status") int status,@Param("id") Long id);

    /**
     * 修改菜品信息
     * @param dish
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 查询分类菜品
     * @param categoryId
     * @return
     */
    @Select("SELECT * FROM dish WHERE category_id=#{categoryId}")
    List<DishVO> selectByCategoryId(Long categoryId);

    /**
     * 查询状态
     * @param dishIds
     * @return
     */
    List<Integer> selectStatusByIds(@Param("ids") List<Long> dishIds);

}
