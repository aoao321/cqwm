package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.sky.constant.MessageConstant.SETMEAL_ENABLE_FAILED;
import static com.sky.constant.MessageConstant.SETMEAL_ON_SALE;

/**
 * @author aoao
 * @create 2025-04-06-18:04
 */
@Service
@Slf4j
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页插件
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        //查询数据
        List<SetmealVO> setmeals = setmealMapper.selectPage(setmealPageQueryDTO);
        //分页
        PageInfo<SetmealVO> pageInfo = new PageInfo<>(setmeals);
        List<SetmealVO> records = pageInfo.getList();
        long total = pageInfo.getTotal();
        //装入PageResult中
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);
        return pageResult;
    }

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        //注入setmeal中
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //插入并且需要主键回显
        try {
            setmealMapper.insert(setmeal);
            log.info("插入数据成功，Setmeal: {}", setmeal);
        } catch (Exception e) {
            log.error("插入数据失败，Setmeal: {}", setmeal, e);
        }
        Long setmealId = setmeal.getId();
        //获取套餐菜品集合
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            Iterator<SetmealDish> iterator = setmealDishes.iterator();
            while (iterator.hasNext()) {
                SetmealDish setmealDish = iterator.next();
                setmealDish.setSetmealId(setmealId);
            }
        }
        //批量插入setmeal_dish关联表
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //根据id查询套餐数据
        Setmeal setmeal = setmealMapper.selectById(id);
        //查询套餐关联的菜品
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        //放入VO中
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        //将DTO注入entity
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //获取setmealId
        Long setmealId = setmeal.getId();
        //更新
        setmealMapper.update(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            //将所有旧的套餐里的菜品删除
            List<Long> setmealIds = new ArrayList<>();
            setmealIds.add(setmealId);
            setmealDishMapper.deleteBySetmealIds(setmealIds);
            //给新的菜品附上套餐id
            Iterator<SetmealDish> iterator = setmealDishes.iterator();
            while (iterator.hasNext()) {
                SetmealDish setmealDish = iterator.next();
                setmealDish.setSetmealId(setmealId);
            }
        }
        //最后批量插入
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void starOrStop(Integer status, Long id) {
        //根据id先查询出菜品的状态,并设置状态码
        Setmeal setmeal = setmealMapper.selectById(id);
        //菜品未起售，套餐则也不能起售
        //根据id从setmeal_dish表中查出菜品dish_id
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);
        if (setmealDishes != null && setmealDishes.size() > 0) {
            //遍历setmealDishes,如果存在状态为0的菜品则停用失败
            List<Long> dishIds = new ArrayList<>();//用于获取dishId集合
            Iterator<SetmealDish> iterator = setmealDishes.iterator();
            while (iterator.hasNext()) {
                SetmealDish setmealDish = iterator.next();
                dishIds.add(setmealDish.getDishId());//获取所有和套餐相关联的菜品id集合
            }
            //查询菜品状态
            List<Integer> dishStatus = dishMapper.selectStatusByIds(dishIds);
            Iterator<Integer> integerIterator = dishStatus.iterator();
            while (integerIterator.hasNext()) {
                if(integerIterator.next()==0){//如果有停售中的状态
                    throw new SetmealEnableFailedException(SETMEAL_ENABLE_FAILED);//抛出异常
                }
            }
            if (!setmeal.getStatus().equals(status)) {//状态和之前不一样
                setmealMapper.updateStatus(status, id);//更新状态
            }
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //判断套餐是否起售，起售则不能删除
        Iterator<Long> iterator = ids.iterator();//遍历
        while (iterator.hasNext()) {
            Long id = iterator.next();
            Setmeal setmeal = setmealMapper.selectById(id);//获得套餐对象
            if(setmeal.getStatus() == 1){
                iterator.remove();//移除不能删除的套餐
                throw new SetmealEnableFailedException(SETMEAL_ON_SALE);
            }
        }
        //删除setmeal_dish套餐中所有关联的菜品
        setmealDishMapper.deleteBySetmealIds(ids);
        //删除setmeal套餐
        setmealMapper.deleteByIds(ids);
    }
}
