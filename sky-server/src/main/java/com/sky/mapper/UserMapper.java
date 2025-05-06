package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

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

    @Select("SELECT * FROM user WHERE id=#{userId}")
    User getById(Long userId);
}
