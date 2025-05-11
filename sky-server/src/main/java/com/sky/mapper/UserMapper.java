package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

import java.util.List;

/**
 * @author aoao
 * @create 2025-04-13-18:44
 */
@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("SELECT * FROM user WHERE openid=#{openid}")
    User selectByOpenid(String openid);

    /**
     * 插入用户
     * @param user
     */
    void insert(User user);

    /**
     * userId查询
     * @param userId
     * @return
     */
    @Select("SELECT * FROM user WHERE id=#{userId}")
    User getById(Long userId);

    /**
     * 统计日期内的用户数量
     * @param dateList
     * @return
     */
    List<Long> countByDate(@Param("dateList") List<String> dateList);

    /**
     * 统计每天新增用户
     * @param dateList
     * @return
     */
    List<Long> countNewByDate(@Param("dateList") List<String> dateList);
}
