package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("SELECT * FROM employee WHERE username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工
     * @param employee
     */
    @Insert("INSERT INTO employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insertEmployee(Employee employee);

    /**
     * 分页查询
     * @param name
     * @return
     */
    List<Employee> selectEmployeePage(@Param("name") String name);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Select("SELECT * FROM employee WHERE id = #{id}")
    Employee selectEmployeeById(Long id);

    /**
     * 根据员工id更新员工信息
     * @param employee
     */
    @Update("UPDATE employee SET name=#{name},username=#{username},phone=#{phone},sex=#{sex},id_number=#{idNumber},update_time=#{updateTime},update_user=#{updateUser} WHERE id=#{id}")
    void updateEmployee(Employee employee);

    /**
     * 修改员工账号状态
     * @param status
     * @param id
     */
    @Update("UPDATE employee SET status=#{status} WHERE id=#{id}")
    void updateEmployeeStatus(@Param("status") int status,@Param("id") Long id);

    /**
     * 修改密码
     * @param employee
     */
    @Update("UPDATE employee SET password=#{password} WHERE id=#{id}")
    void updateEmployeePassword( Employee employee);
}
