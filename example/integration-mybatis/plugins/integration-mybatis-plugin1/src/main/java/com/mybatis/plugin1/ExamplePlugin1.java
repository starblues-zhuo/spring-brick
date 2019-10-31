package com.mybatis.plugin1;

import com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ExamplePlugin1 extends BasePlugin implements SpringBootMybatisConfig {

    private final Set<String> mybatisMapperXmlLocationsMatch = new HashSet<>();
    private final Set<String> typeAliasesPackage = new HashSet<>();


    public ExamplePlugin1(PluginWrapper wrapper) {
        super(wrapper);
        mybatisMapperXmlLocationsMatch.add("classpath:mapper/*Mapper.xml");
        typeAliasesPackage.add("com.mybatis.plugin1.entity");
    }

    @Override
    protected void startEvent() {

    }

    @Override
    protected void deleteEvent() {

    }

    @Override
    protected void stopEvent() {

    }

    @Override
    public Set<String> mybatisMapperXmlLocationsMatch() {
        return mybatisMapperXmlLocationsMatch;
    }


    @Override
    public Set<String> typeAliasesPackage() {
        return typeAliasesPackage;
    }
}
