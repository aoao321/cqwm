package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author aoao
 * @create 2025-04-05-19:18
 */
@Slf4j
@Transactional
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    public void save(DishDTO dishDTO) {
        //向菜品表插入1条数据
        //将dishDTO放入dish中
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //执行完之后要主键回显
        dishMapper.insert(dish);
        Long DishId = dish.getId();
        //获取口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            //遍历集合
            Iterator<DishFlavor> iterator = flavors.iterator();
            while (iterator.hasNext()){
                DishFlavor dishFlavor = iterator.next();
                dishFlavor.setDishId(DishId);
            }
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //分页
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        List<DishVO> dishList = dishMapper.selectDishByName(dishPageQueryDTO);
        //封装到pageInfo中
        PageInfo<DishVO> page = new PageInfo<>(dishList);
        //获得总页数
        long total = page.getTotal();
        //获得分页信息
        List<DishVO> records = page.getList();
        //封装进PageResult中
        PageResult pageResult = new PageResult(total,records);
        return pageResult;
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //遍历菜品
        Iterator<Long> iterator = ids.iterator();
        while (iterator.hasNext()){
            Long id = iterator.next();
            //判断当前菜品的起售状态
            if(dishMapper.selectById(id).getStatus() == 1){
                //停售的则不能删除
                iterator.remove();
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }//判断套餐表是否有关联的菜品
            else if (setmealDishMapper.count(id) > 0) {
                iterator.remove();
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        //删除菜品表中菜品
        dishMapper.deleteByIds(ids);
        //删除口味数据
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public void starOrStop(int status, Long id) {
        //根据id先查询出员工的状态,并设置状态码
        Dish dish = dishMapper.selectById(id);
        if(!dish.getStatus().equals(status)){
            dishMapper.updateStatus(status,id);
        }
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //查询菜品数据
        Dish dish = dishMapper.selectById(id);
        //查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);
        //封装到vo里
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        //将dishDTO放入dish中
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //更新表
        dishMapper.update(dish);
        System.out.println("----------------------------");
        //先把以前的口味删除在重新插入口味
        List<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteByDishIds(ids);
        System.out.println("----------------------------");
        //获取口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            //遍历集合
            Iterator<DishFlavor> iterator = flavors.iterator();
            while (iterator.hasNext()){
                DishFlavor dishFlavor = iterator.next();
                dishFlavor.setDishId(dish.getId());
            }
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }


}
