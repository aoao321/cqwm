package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-05-19:11
 */

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品接口")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        //调用service层存入菜品信息
        dishService.save(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        //调用service层实现分页查询
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result<String> delete(@RequestParam List<Long> ids) {
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用菜品状态")
    public Result<String> starOrStop(@PathVariable int status, @Param("id") Long id ) {
        //调用service层启动或者禁用员工账号
        dishService.starOrStop(status,id);
        return Result.success();
    }

    @GetMapping("{id}")
    @ApiOperation("根据id查询菜品信息")
    public Result<DishVO> get(@PathVariable Long id) {
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品信息")
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        //调用service层存入菜品信息
        dishService.update(dishDTO);
        return Result.success();
    }
}
