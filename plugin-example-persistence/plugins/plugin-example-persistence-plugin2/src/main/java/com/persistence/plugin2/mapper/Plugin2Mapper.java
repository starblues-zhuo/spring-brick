package com.persistence.plugin2.mapper;

import com.gitee.starblues.extension.mybatis.annotation.Mapper;
import com.persistence.plugin2.entity.Plugin2;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
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

}
