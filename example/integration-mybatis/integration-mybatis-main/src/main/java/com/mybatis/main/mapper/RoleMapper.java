package com.mybatis.main.mapper;

import com.mybatis.main.entity.Role;
import org.apache.ibatis.annotations.Insert;
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
public interface RoleMapper {

    /**
     * 得到角色列表
     * @return List
     */
    List<Role> getList();

    @Select("select * from role")
    List<Role> getSqlList();

    @Insert("INSERT INTO plugin1 VALUES (#{id}, #{name})")
    void insert(@Param("id") String id, @Param("name") String name);

}
