package com.mybatis.plugin2;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.extension.mybatis.SpringBootMybatisConfig;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 * @since 2020-12-18
 */
@ConfigDefinition
public class MybatisConfig2 implements SpringBootMybatisConfig {

    @Override
    public Set<String> entityPackage() {
        Set<String> typeAliasesPackage = new HashSet<>();
        typeAliasesPackage.add("com.mybatis.plugin2.entity");
        return typeAliasesPackage;
    }

    @Override
    public Set<String> xmlLocationsMatch() {
        Set<String> xmlLocationsMatch = new HashSet<>();
        xmlLocationsMatch.add("classpath:mapper/*Mapper.xml");
        return xmlLocationsMatch;
    }
}
