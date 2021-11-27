package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.PropertyKey;
import com.gitee.starblues.factory.process.pipe.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件的ApplicationContext 处理
 * 主要进行插件bean的扫描
 * @author starBlues
 * @version 2.4.3
 */
public class PluginPipeApplicationContextProcessor implements PluginPipeProcessor{

    private final static Logger logger = LoggerFactory.getLogger(PluginPipeApplicationContextProcessor.class);

    private final List<PluginBeanRegistrar> pluginBeanDefinitionRegistrars = new ArrayList<>();
    private final ApplicationContext mainApplicationContext;


    public PluginPipeApplicationContextProcessor(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public void initialize() throws Exception {
        pluginBeanDefinitionRegistrars.add(new SpringBootConfigFileRegistrar(mainApplicationContext));
        pluginBeanDefinitionRegistrars.add(new PluginInsetBeanRegistrar());
        pluginBeanDefinitionRegistrars.add(new ConfigBeanRegistrar());
        pluginBeanDefinitionRegistrars.add(new ConfigFileBeanRegistrar(mainApplicationContext));
        pluginBeanDefinitionRegistrars.add(new BasicBeanRegistrar());
        pluginBeanDefinitionRegistrars.add(new InvokeBeanRegistrar());
        pluginBeanDefinitionRegistrars.addAll(ExtensionInitializer.getPluginBeanRegistrarExtends());
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        // 进行bean注册
        for (PluginBeanRegistrar pluginBeanDefinitionRegistrar : pluginBeanDefinitionRegistrars) {
            pluginBeanDefinitionRegistrar.registry(pluginRegistryInfo);
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            registerMustDependencies(pluginApplicationContext, pluginRegistryInfo);
            installPluginAutoConfiguration(pluginApplicationContext, pluginRegistryInfo);
            pluginApplicationContext.refresh();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        // 向插件静态容器中新增插件的ApplicationContext
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        PluginInfoContainers.addPluginApplicationContext(pluginId, pluginApplicationContext);
    }

    /**
     * 安装插件定义的自动装载配置类
     * @param pluginApplicationContext 插件ApplicationContext
     * @param pluginRegistryInfo 插件注册信息
     */
    private void installPluginAutoConfiguration(GenericApplicationContext pluginApplicationContext,
                                                PluginRegistryInfo pluginRegistryInfo) throws ClassNotFoundException {
        Set<String> installAutoConfigClassString = pluginRegistryInfo.getPluginBinder()
                .bind(PropertyKey.INSTALL_AUTO_CONFIG_CLASS, Bindable.setOf(String.class))
                .orElseGet(()->null);

        if(ObjectUtils.isEmpty(installAutoConfigClassString)){
            return;
        }

        // 注册AutoConfigurationPackages, 用于插件可自动配置
        AutoConfigurationPackages.register(pluginApplicationContext.getDefaultListableBeanFactory(),
                pluginRegistryInfo.getBasePlugin().scanPackage());

        Set<Class<?>> autoConfigurationClassSet = new HashSet<>(installAutoConfigClassString.size());
        ClassLoader pluginClassLoader = pluginRegistryInfo.getPluginClassLoader();
        for (String autoConfigClassPackage : installAutoConfigClassString) {
            Class<?> aClass = Class.forName(autoConfigClassPackage, false, pluginClassLoader);
            autoConfigurationClassSet.add(aClass);
        }
        for (Class<?> autoConfigurationClass : autoConfigurationClassSet) {
            pluginApplicationContext.registerBean(autoConfigurationClass);
        }
    }

    /**
     * 定义一些spring-boot中一些必要注册的依赖
     * @param pluginApplicationContext pluginApplicationContext
     * @param pluginRegistryInfo pluginRegistryInfo
     */
    private void registerMustDependencies(GenericApplicationContext pluginApplicationContext,
                                          PluginRegistryInfo pluginRegistryInfo){
        // 注册AutoConfigurationPackages, 用于插件可自动配置
        AutoConfigurationPackages.register(pluginApplicationContext.getDefaultListableBeanFactory(),
                pluginRegistryInfo.getBasePlugin().scanPackage());
        // 注册 ConfigurationPropertiesBindingPostProcessor
        ConfigurationPropertiesBindingPostProcessor.register(pluginApplicationContext);
    }


    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        for (PluginBeanRegistrar registrar : pluginBeanDefinitionRegistrars) {
            try {
                registrar.unRegistry(pluginRegistryInfo);
            } catch (Exception e){
                logger.error("Plugin '{}'-'{}' unRegistry failure.",
                        pluginRegistryInfo.getPluginWrapper().getPluginId(),
                        registrar.getClass().getName());
            }
        }
    }

}
