package com.gitee.starblues.factory.process.pipe;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.*;
import com.gitee.starblues.factory.process.pipe.classs.group.AutoConfigurationSelectorGroup;
import com.gitee.starblues.realize.AutoConfigurationSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 插件的ApplicationContext 处理
 * 主要进行插件bean的扫描
 * @author starBlues
 * @version 2.4.0
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
        pluginBeanDefinitionRegistrars.add(new SpringBootConfigFileRegistrar());
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
            installPluginAutoConfiguration(pluginApplicationContext, pluginRegistryInfo);
            Thread.currentThread().setContextClassLoader(pluginRegistryInfo.getPluginClassLoader());
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
                                                PluginRegistryInfo pluginRegistryInfo) {
        List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(AutoConfigurationSelectorGroup.ID);
        if(groupClasses == null || groupClasses.isEmpty()){
            return;
        }
        PluginAutoConfigurationInstaller installer = new PluginAutoConfigurationInstaller();
        for (Class<?> groupClass : groupClasses) {
            try {
                Object o = groupClass.newInstance();
                if(o instanceof AutoConfigurationSelector){
                    AutoConfigurationSelector selector = (AutoConfigurationSelector) o;
                    selector.select(installer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Set<Class<?>> autoConfigurationClassSet = installer.getAutoConfigurationClassSet();
        if(autoConfigurationClassSet.isEmpty()){
            return;
        }
        for (Class<?> autoConfigurationClass : autoConfigurationClassSet) {
            pluginApplicationContext.registerBean(autoConfigurationClass);
        }
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
