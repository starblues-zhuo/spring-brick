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
        pluginBeanDefinitionRegistrars.add(new OneselfListenerBeanRegistrar());
        pluginBeanDefinitionRegistrars.addAll(ExtensionInitializer.getPluginBeanRegistrarExtends());
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        AnnotationConfigApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        pluginApplicationContext.getDefaultListableBeanFactory().registerSingleton("p",
                pluginApplicationContext);
        // 进行bean注册
        for (PluginBeanRegistrar pluginBeanDefinitionRegistrar : pluginBeanDefinitionRegistrars) {
            pluginBeanDefinitionRegistrar.registry(pluginRegistryInfo);
        }
        pluginApplicationContext.refresh();
        buildPluginApplicationContext(pluginRegistryInfo);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        PluginInfoContainers.removePluginApplicationContext(pluginRegistryInfo.getPluginWrapper().getPluginId());
    }

    private void buildPluginApplicationContext(PluginRegistryInfo pluginRegistryInfo){
        GenericApplicationContext parentApplicationContext = pluginRegistryInfo.getMainApplicationContext();
        AnnotationConfigApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();

        PluginUtils pluginUtils = new PluginUtils(parentApplicationContext,
                pluginApplicationContext,
                pluginRegistryInfo.getPluginWrapper().getDescriptor());
        parentApplicationContext.getBeanFactory().registerSingleton(
                pluginUtils.getClass().getName(), pluginUtils);
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        PluginInfoContainers.addPluginApplicationContext(pluginId, pluginApplicationContext);
    }


}
