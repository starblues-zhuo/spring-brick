package com.mybatis.plugin2.mapper;

import com.mybatis.plugin2.entity.Plugin2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@Mapper
public interface Plugin2Mapper {


    /**
     * 得到角色列表
     * @return List
     */
    List<Plugin2> getList();

    /**
     * 通过id获取数据
     * @param id id
     * @return Plugin2
     */
    Plugin2 getById(@Param("id") String id);


    @Insert("INSERT INTO plugin2 VALUES (#{id}, #{name})")
    void save(@Param("id") String id, @Param("name") String name);

}
