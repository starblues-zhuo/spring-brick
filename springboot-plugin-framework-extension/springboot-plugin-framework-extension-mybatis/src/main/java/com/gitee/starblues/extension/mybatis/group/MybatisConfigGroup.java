package com.gitee.starblues.extension.mybatis.group;

import com.gitee.starblues.extension.mybatis.SpringBootMybatisConfig;
import com.gitee.starblues.extension.mybatis.mybatisplus.SpringBootMybatisPlusConfig;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import com.gitee.starblues.realize.BasePlugin;
import org.springframework.util.ClassUtils;

import java.util.Set;

/**
 * mybatis 配置分组
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-11
 */
public class MybatisConfigGroup implements PluginClassGroupExtend {

    public static final String KEY = "plugin_mybatis_config";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public String groupId() {
        return "MybatisConfigGroup";
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        if(aClass == null){
            return false;
        }
        Set<Class<?>> allInterfacesForClassAsSet = ClassUtils.getAllInterfacesForClassAsSet(aClass);

        return allInterfacesForClassAsSet.contains(SpringBootMybatisConfig.class)
                || allInterfacesForClassAsSet.contains(SpringBootMybatisPlusConfig.class);

    }
}
