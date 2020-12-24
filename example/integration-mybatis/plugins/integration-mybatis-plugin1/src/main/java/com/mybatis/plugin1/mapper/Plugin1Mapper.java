package com.mybatis.plugin1.mapper;

import com.mybatis.plugin1.entity.Plugin1;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Mapper
public interface Plugin1Mapper {


    /**
     * 得到角色列表
     * @return List
     */
    List<Plugin1> getList();

    /**
     * 通过id获取数据
     * @param id id
     * @return Plugin2
     */
    Plugin1 getById(@Param("id") String id);


    List<Plugin1> getByCondition(Plugin1 plugin1);

    @Insert("INSERT INTO plugin1 VALUES (#{id}, #{name})")
    void insert(@Param("id") String id, @Param("name") String name);

}
