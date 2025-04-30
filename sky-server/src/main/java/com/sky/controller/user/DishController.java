package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        //查询redis中是否存在菜品数据
        String key = "dish_"+categoryId;
        //存在，直接返回
        List<DishVO> dishVO = (List<DishVO>)redisTemplate.opsForValue().get(key);
        if (dishVO != null&&dishVO.size()>0) {
            return Result.success(dishVO);
        }
        //不存在，查询数据库,加入redis中
        dishVO = dishService.list(categoryId);
        redisTemplate.opsForValue().set(key,dishVO);
        return Result.success(dishVO);
    }

}
