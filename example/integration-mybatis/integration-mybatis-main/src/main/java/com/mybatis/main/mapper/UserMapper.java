package com.mybatis.main.mapper;

import com.mybatis.main.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@Repository
public interface UserMapper {

    /**
     * 得到用户列表
     * @return List
     */
    List<User> getList();

    /**
     * 通过id得到用户
     * @param id id
     * @return User
     */
    @Select("select * from user where user_id = #{id}")
    User getById(@Param("id") String id);

}
