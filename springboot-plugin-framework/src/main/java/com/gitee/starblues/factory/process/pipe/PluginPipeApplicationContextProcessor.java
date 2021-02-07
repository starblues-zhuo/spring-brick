package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.ServletContext;
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
            Thread.currentThread().setContextClassLoader(pluginRegistryInfo.getPluginClassLoader());
            pluginApplicationContext.refresh();
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }

        // 向插件静态容器中新增插件的ApplicationContext
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        PluginInfoContainers.addPluginApplicationContext(pluginId, pluginApplicationContext);
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
