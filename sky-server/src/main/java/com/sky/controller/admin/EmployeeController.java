package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }


    @PostMapping("/logout")
    @ApiOperation("员工退出")
    public Result<String> logout() {
        return Result.success();
    }

    @PostMapping
    @ApiOperation("添加员工")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        //调用service层
        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("page")
    @ApiOperation("分页查询员工信息")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        //调用service来实现分页
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("{id}")
    @ApiOperation("根据id查询员工")
    public Result<Employee> getById(@PathVariable Long id) {
        //调用service层
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result<String> edit(@RequestBody EmployeeDTO employeeDTO) {
        //调用service层更新员工信息
        employeeService.edit(employeeDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用员工账号")
    public Result<String> starOrStop(@PathVariable int status, @Param("id") Long id ) {
        //调用service层启动或者禁用员工账号
        employeeService.starOrStop(status,id);
        return Result.success();
    }

    @PutMapping("/editPassword")
    @ApiOperation("修改员工密码")
    public Result<String> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        //修改账号密码
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }
}
