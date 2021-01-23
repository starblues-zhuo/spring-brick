package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.*;
import com.gitee.starblues.realize.PluginUtils;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.*;

/**
 * 插件的ApplicationContext 处理
 * 主要进行插件bean的扫描
 * @author starBlues
 * @version 1.0
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
        addBeanExtend(pluginRegistryInfo);
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(pluginRegistryInfo.getDefaultPluginClassLoader());
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


    /**
     * 向插件ApplicationContext容器中添加扩展的bean
     * @param pluginRegistryInfo 插件注册信息
     */
    private void addBeanExtend(PluginRegistryInfo pluginRegistryInfo){
        GenericApplicationContext parentApplicationContext = pluginRegistryInfo.getMainApplicationContext();
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        PluginUtils pluginUtils = new PluginUtils(parentApplicationContext,
                pluginApplicationContext,
                pluginRegistryInfo.getPluginWrapper().getDescriptor());
        String name = pluginUtils.getClass().getName();
        pluginApplicationContext.getBeanFactory().registerSingleton(name, pluginUtils);
        pluginRegistryInfo.addExtension("PluginUtilsName", name);
    }


    /**
     * 移除扩展绑定
     * @param pluginRegistryInfo 插件注册信息
     */
    public static void removeBeanExtend(PluginRegistryInfo pluginRegistryInfo) {
        String pluginUtilsName = pluginRegistryInfo.getExtension("PluginUtilsName");
        if(StringUtils.isNullOrEmpty(pluginUtilsName)){
            return;
        }
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        pluginApplicationContext.getDefaultListableBeanFactory().destroySingleton(pluginUtilsName);
    }

}
