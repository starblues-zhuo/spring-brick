package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.ComponentGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigurationGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.RepositoryGroup;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 基础bean注册
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class BasicBeanProcessor implements PluginPipeProcessor {

    private static final String KEY = "BasicBeanProcessor";

    private final SpringBeanRegister springBeanRegister;

    public BasicBeanProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        this.springBeanRegister = new SpringBeanRegister(applicationContext);
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = new HashSet<>();
        List<Class<?>> springComponents = pluginRegistryInfo
                .getGroupClasses(ComponentGroup.SPRING_COMPONENT);
        List<Class<?>> springConfigurations = pluginRegistryInfo
                .getGroupClasses(ConfigurationGroup.SPRING_CONFIGURATION);
        List<Class<?>> springRepository = pluginRegistryInfo
                .getGroupClasses(RepositoryGroup.SPRING_REPOSITORY);
        register(pluginRegistryInfo, springComponents, beanNames);
        register(pluginRegistryInfo, springConfigurations, beanNames);
        register(pluginRegistryInfo, springRepository, beanNames);
        pluginRegistryInfo.addProcessorInfo(KEY, beanNames);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = pluginRegistryInfo.getProcessorInfo(KEY);
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        if(beanNames != null){
            for (String beanName : beanNames) {
                springBeanRegister.unregister(pluginId, beanName);
            }
        }
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
        if(classes == null){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        for (Class<?> aClass : classes) {
            if(aClass == null){
                continue;
            }
            String beanName = springBeanRegister.register(pluginId, aClass);
            beanNames.add(beanName);
        }
    }



}
