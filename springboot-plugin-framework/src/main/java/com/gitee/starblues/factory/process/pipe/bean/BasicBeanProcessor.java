package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.ComponentGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigurationGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.OneselfListenerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.RepositoryGroup;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
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
    private final ApplicationContext applicationContext;

    public BasicBeanProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        this.applicationContext = applicationContext;
        this.springBeanRegister = new SpringBeanRegister(applicationContext);
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

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<String> beanNames = pluginRegistryInfo.getProcessorInfo(KEY);
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        if(beanNames != null){
            for (String beanName : beanNames) {
                springBeanRegister.unregister(pluginId, beanName);
            }
            removeService(new ArrayList<>(beanNames));
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
        if(classes == null || classes.isEmpty()){
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

    @SuppressWarnings("unchecked")
    private void removeService(List<String> beanNames) {
        AbstractAutoProxyCreator proxyCreator = applicationContext.getBean(AbstractAutoProxyCreator.class);
        Map<Object, Class<?>> proxyTypes;
        Map<Object, Boolean> advisedBeans;
        try {
            Field proxyTypesField = proxyCreator.getClass().getDeclaredField("proxyTypes");
            if (!proxyTypesField.isAccessible()) {
                proxyTypesField.setAccessible(true);
            }
            proxyTypes = (Map<Object, Class<?>>) proxyTypesField.get(proxyCreator);

            Field advisedBeansField = proxyCreator.getClass().getDeclaredField("advisedBeans");
            if (!advisedBeansField.isAccessible()) {
                advisedBeansField.setAccessible(true);
            }
            advisedBeans = (Map<Object, Boolean>) advisedBeansField.get(proxyCreator);
        } catch (Exception e) {
            return;
        }
        beanNames.forEach(beanName -> {
            proxyTypes.remove(beanName);
            advisedBeans.remove(beanName);
        });
    }

}
