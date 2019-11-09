package com.mybatis.plugin1.entity;

import org.apache.ibatis.type.Alias;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Alias("plugin1")
public class Plugin1 {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
