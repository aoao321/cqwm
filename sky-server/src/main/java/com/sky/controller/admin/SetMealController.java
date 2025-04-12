package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-06-17:33
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetMealController {

    @Autowired
    private SetmealService setmealService;

    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页查询
        PageResult pageResult = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("添加套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        //新增菜品
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> get(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return Result.success();
    }

    @PostMapping("status/{status}")
    @ApiOperation("修改套餐起售状态")
    public Result updateStatus(@PathVariable Integer status, @Param("id") Long id) {
        //调用service层启动或者禁用员工账号
        setmealService.starOrStop(status,id);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteBatch(@Param("ids") List<Long> ids) {
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
