package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.*;
import com.gitee.starblues.realize.PluginUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件的ApplicationContext 处理
 * 主要进行插件bean的扫描
 * @author starBlues
 * @version 1.0
 */
public class PluginApplicationContextProcessor implements PluginPipeProcessor{

    private final List<PluginBeanRegistrar> pluginBeanDefinitionRegistrars = new ArrayList<>();
    private final ApplicationContext mainApplicationContext;

    public PluginApplicationContextProcessor(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public void initialize() throws Exception {
        pluginBeanDefinitionRegistrars.add(new ConfigBeanRegistrar());
        pluginBeanDefinitionRegistrars.add(new ConfigFileBeanRegistrar(mainApplicationContext));
        pluginBeanDefinitionRegistrars.add(new BasicBeanRegistrar());
        pluginBeanDefinitionRegistrars.addAll(ExtensionInitializer.getPluginBeanRegistrarExtends());
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        pluginApplicationContext.getDefaultListableBeanFactory().registerSingleton("p",
                pluginApplicationContext);
        // 进行bean注册
        for (PluginBeanRegistrar pluginBeanDefinitionRegistrar : pluginBeanDefinitionRegistrars) {
            pluginBeanDefinitionRegistrar.registry(pluginRegistryInfo);
        }
        addBeanExtend(pluginRegistryInfo);
        pluginApplicationContext.refresh();
        // 向插件静态容器中新增插件的ApplicationContext
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        PluginInfoContainers.addPluginApplicationContext(pluginId, pluginApplicationContext);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        for (PluginBeanRegistrar pluginBeanDefinitionRegistrar : pluginBeanDefinitionRegistrars) {
            pluginBeanDefinitionRegistrar.unRegistry(pluginRegistryInfo);
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
        parentApplicationContext.getBeanFactory().registerSingleton(
                pluginUtils.getClass().getName(), pluginUtils);
    }

}
