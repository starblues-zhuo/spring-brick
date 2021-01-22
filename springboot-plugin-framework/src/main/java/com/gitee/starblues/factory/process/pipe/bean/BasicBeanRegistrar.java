package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.classs.group.ComponentGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigurationGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.OneselfListenerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.RepositoryGroup;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础bean注册
 *
 * @author starBlues
 * @version 2.4.0
 */
public class BasicBeanRegistrar implements PluginBeanRegistrar {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicBeanRegistrar.class);

    private static final String KEY = "BasicBeanProcessor";

    public BasicBeanRegistrar(){
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = new HashSet<>();
        List<Class<?>> springComponents = pluginRegistryInfo
                .getGroupClasses(ComponentGroup.GROUP_ID);
        List<Class<?>> springConfigurations = pluginRegistryInfo
                .getGroupClasses(ConfigurationGroup.GROUP_ID);
        List<Class<?>> springRepository = pluginRegistryInfo
                .getGroupClasses(RepositoryGroup.GROUP_ID);
        List<Class<?>> oneselfListener = pluginRegistryInfo.getGroupClasses(OneselfListenerGroup.GROUP_ID);

        register(pluginRegistryInfo, springComponents, beanNames);
        register(pluginRegistryInfo, springConfigurations, beanNames);
        register(pluginRegistryInfo, springRepository, beanNames);
        register(pluginRegistryInfo, oneselfListener, beanNames);
        pluginRegistryInfo.addProcessorInfo(KEY, beanNames);
    }

    /**
     * 往Spring注册bean
     * @param pluginRegistryInfo 插件注册的信息
     * @param classes 要注册的类集合
     * @param beanNames 存储bean名称集合
     */
    private void register(PluginRegistryInfo pluginRegistryInfo,
                          List<Class<?>> classes,
                          Set<String> beanNames){
        if(classes == null || classes.isEmpty()){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        for (Class<?> aClass : classes) {
            if(aClass == null){
                continue;
            }
            String beanName = springBeanRegister.register(pluginId, aClass);
            beanNames.add(beanName);
        }
    }



}
