package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.factory.FactoryType;

/**
 * SpringbootMybatis 插件扩展工厂类型
 *
 * @author zhangzhuo
 * @version 1.0
 */
public enum SpringbootMybatisFactoryType implements FactoryType {


    /**
     * mybatis xml 工厂
     */
    MYBATIS_XML("MYBATIS_XML");

    private String key;

    SpringbootMybatisFactoryType(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }

}
