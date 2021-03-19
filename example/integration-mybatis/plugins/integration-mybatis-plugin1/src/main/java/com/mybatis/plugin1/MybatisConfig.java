package com.mybatis.plugin1;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.extension.mybatis.SpringBootMybatisConfig;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.realize.ConfigDefinitionTip;
import com.mybatis.main.config.PluginBeanConfig;
import com.mybatis.plugin1.config.Plugin1Config;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 * @since 2020-12-18
 */
@ConfigDefinition
public class MybatisConfig implements SpringBootMybatisConfig {

    private ConfigDefinitionTip configDefinitionTip;

    @Override
    public Set<String> entityPackage() {
        // 测试 configDefinitionTip 的使用
//        PluginInfo currentPluginInfo = configDefinitionTip.getCurrentPluginInfo();
//        System.out.println("MybatisConfig=" + currentPluginInfo);
//        Plugin1Config plugin1Config = configDefinitionTip.getOtherConfigDefinition(Plugin1Config.class);
//        System.out.println("plugin1Config=" + plugin1Config);
//        ApplicationContext mainApplication = configDefinitionTip.getMainApplication();
//        PluginBeanConfig bean = mainApplication.getBean(PluginBeanConfig.class);
//        System.out.println("PluginBeanConfig" + bean);


        Set<String> typeAliasesPackage = new HashSet<>();
        typeAliasesPackage.add("com.mybatis.plugin1.entity");
        return typeAliasesPackage;
    }

    @Override
    public Set<String> xmlLocationsMatch() {
        Set<String> xmlLocationsMatch = new HashSet<>();
        xmlLocationsMatch.add("classpath:mapper/*Mapper.xml");
        return xmlLocationsMatch;
    }
}
