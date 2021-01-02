package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.ComponentGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.ConfigurationGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.OneselfListenerGroup;
import com.gitee.starblues.factory.process.pipe.classs.group.RepositoryGroup;
import java.lang.reflect.Field;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;

/**
 * 基础bean注册
 *
 * @author starBlues
 * @version 2.3
 */
public class BasicBeanProcessor implements PluginPipeProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicBeanProcessor.class);

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
            removeProxyBeanName(beanNames, pluginId);
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

    /**
     * 移除代理类的Bean
     * @param beanNames bean名称
     * @param pluginId 插件id
     */
    @SuppressWarnings("unchecked")
    private void removeProxyBeanName(Set<String> beanNames, String pluginId) {
        AbstractAutoProxyCreator proxyCreator = applicationContext.getBean(AbstractAutoProxyCreator.class);
        try {
            Class<? extends AbstractAutoProxyCreator> aClass = proxyCreator.getClass();
            Field proxyTypesField = ReflectionUtils.findField(aClass, "proxyTypes");
            Map<Object, Class<?>> proxyTypes = null;
            if(proxyTypesField != null){
                if (!proxyTypesField.isAccessible()) {
                    proxyTypesField.setAccessible(true);
                }
                proxyTypes = (Map<Object, Class<?>>) proxyTypesField.get(proxyCreator);
            }

            Field advisedBeansField = ReflectionUtils.findField(aClass, "advisedBeans");
            Map<Object, Boolean> advisedBeans = null;
            if(advisedBeansField != null){
                if (!advisedBeansField.isAccessible()) {
                    advisedBeansField.setAccessible(true);
                }
                advisedBeans = (Map<Object, Boolean>) advisedBeansField.get(proxyCreator);
            }

            for (String beanName : beanNames) {
                if(proxyTypes != null){
                    proxyTypes.remove(beanName);
                }
                if(advisedBeans != null){
                    advisedBeans.remove(beanName);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Remove plugin '{}' proxy bean failure. {}", pluginId, e.getMessage(), e);
        }
    }

}
