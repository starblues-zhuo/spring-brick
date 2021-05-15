package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.*;
import com.gitee.starblues.factory.process.pipe.classs.group.AutoConfigurationSelectorGroup;
import com.gitee.starblues.realize.AutoConfigurationSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
            registerPluginSpringBootConfigProcessor(pluginRegistryInfo, pluginApplicationContext);
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
    /**
     * 注册用于装载插件中springboot自带的配置文件
     * @param pluginRegistryInfo 插件注册信息
     */
    private void registerPluginSpringBootConfigProcessor(PluginRegistryInfo pluginRegistryInfo, GenericApplicationContext applicationContext) {
        applicationContext.registerBean(
                PluginConfigEnvironmentPostProcessor.class,
                consumer->{
                    consumer.getPropertyValues().add("pluginRegistryInfo",
                            pluginRegistryInfo);
                });
    }


    private static class PluginConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

        //Properties对象
        private final Properties properties = new Properties();
        private PluginRegistryInfo pluginRegistryInfo;

        public void setPluginRegistryInfo(PluginRegistryInfo pluginRegistryInfo) {
            this.pluginRegistryInfo = pluginRegistryInfo;
        }

        @Override
        public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
            ClassLoader pluginClassLoader = pluginRegistryInfo.getPluginWrapper().getPluginClassLoader();
            String s = pluginRegistryInfo.getBasePlugin().springBootConfigFilePath();
            if(s == null){
                return;
            }
            Resource resource = new ClassPathResource(
                    s, pluginClassLoader
            );
            //加载成PropertySource对象，并添加到Environment环境中
            environment.getPropertySources().addLast(loadProfiles(resource));
        }

        //加载单个配置文件
        private PropertySource<?> loadProfiles(Resource resource) {
            if (!resource.exists()) {
                throw new IllegalArgumentException("资源" + resource + "不存在");
            }
            try {
                //从输入流中加载一个Properties对象
                properties.load(resource.getInputStream());
                return new PropertiesPropertySource(resource.getFilename(), properties);
            }catch (IOException ex) {
                throw new IllegalStateException("加载配置文件失败" + resource, ex);
            }
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
