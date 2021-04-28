package com.gitee.starblues.factory.process.pipe;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件自动装配安装操作
 * @author starBlues
 * @version 2.4.3
 */
public class PluginAutoConfigurationInstaller {

    private final Set<Class<?>> autoConfigurationClassSet = new HashSet<>();

    /**
     * 处理该插件的注册
     * @param autoConfigurationClass 自动装载的配置类
     * @return PluginAutoConfigurationInstaller PluginAutoConfigurationInstaller
     */
    public PluginAutoConfigurationInstaller install(Class<?> autoConfigurationClass){
        autoConfigurationClassSet.add(autoConfigurationClass);
        return this;
    }

    /**
     * 得到自动装载的所有类
     * @return autoConfigurationClassSet
     */
    Set<Class<?>> getAutoConfigurationClassSet() {
        return autoConfigurationClassSet;
    }
}
