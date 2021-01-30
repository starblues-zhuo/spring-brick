package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.classs.group.ComponentGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.OneselfListenerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.RepositoryGroup;

import java.util.*;

/**
 * 基础bean注册
 *
 * @author starBlues
 * @version 2.4.0
 */
public class BasicBeanRegistrar implements PluginBeanRegistrar {


    public BasicBeanRegistrar(){
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Class<?>> springComponents = pluginRegistryInfo
                .getGroupClasses(ComponentGroup.GROUP_ID);
        List<Class<?>> springRepository = pluginRegistryInfo
                .getGroupClasses(RepositoryGroup.GROUP_ID);
        List<Class<?>> oneselfListener = pluginRegistryInfo
                .getGroupClasses(OneselfListenerGroup.GROUP_ID);

        register(pluginRegistryInfo, springComponents);
        register(pluginRegistryInfo, springRepository);
        register(pluginRegistryInfo, oneselfListener);
    }

    /**
     * 往Spring注册bean
     * @param pluginRegistryInfo 插件注册的信息
     * @param classes 要注册的类集合
     */
    private void register(PluginRegistryInfo pluginRegistryInfo,
                          List<Class<?>> classes){
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
        }
    }



}
