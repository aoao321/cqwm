package com.sky.service.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.github.pagehelper.IPage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();


        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置状态可用
        employee.setStatus(StatusConstant.ENABLE);
        //设置默认密码“123456”，用MD5加密
        String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);
        //调用mapper
        employeeMapper.insertEmployee(employee);
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        //使用分页插件，传入当前第几页和分页页数
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //查询所有员工信息
        List<Employee> list = employeeMapper.selectEmployeePage(employeePageQueryDTO.getName());
        //封装到PageInfo中
        PageInfo<Employee> page = new PageInfo<>(list);
        //获得总页数
        long total = page.getTotal();
        //获得分页信息
        List<Employee> records = page.getList();
        //封装进PageResult中
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);

        return pageResult;
    }

    @Override
    public Employee getById(Long id) {
        return employeeMapper.selectEmployeeById(id);
    }

    @Override
    public void edit(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //将employeeDTO注入employee中
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置update_time
        employee.setUpdateTime(LocalDateTime.now());
        //设置update_user
        employee.setUpdateUser(BaseContext.getCurrentId());
        //调用mapper
        employeeMapper.updateEmployee(employee);
    }

    @Override
    public void starOrStop(int status, Long id) {
        //根据id先查询出员工的状态,并设置状态码
        Employee employee = employeeMapper.selectEmployeeById(id);
        if(!employee.getStatus().equals(status)){
            employeeMapper.updateEmployeeStatus(status,id);
        }
    }

    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        //查询用户密码
        Employee employee = employeeMapper.selectEmployeeById(passwordEditDTO.getEmpId());
        //进行旧密码核对
        if(employee == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if(DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes()).equals(employee.getPassword())){
            String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());
            //存入数据库中
            employee.setPassword(newPassword);
            employeeMapper.updateEmployeePassword(employee);
        }
    }


}
