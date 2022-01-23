package com.gitee.starblues.bootstrap.processor;


import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.core.launcher.plugin.RegistryInfo;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.spring.WebConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * 处理者上下文
 * @author starBlues
 * @version 3.0.0
 */
public interface ProcessorContext extends RegistryInfo {

    /**
     * 得到入口类对象-SpringPluginBootstrap
     * @return SpringPluginBootstrap
     */
    SpringPluginBootstrap getSpringPluginBootstrap();

    /**
     * 得到插件信息 PluginDescriptor
     * @return PluginDescriptor
     */
    InsidePluginDescriptor getPluginDescriptor();

    /**
     * 得到启动的class类
     * @return Class
     */
    Class<? extends SpringPluginBootstrap> getRunnerClass();

    /**
     * 得到 PluginInteractive
     * @return PluginInteractive
     */
    PluginInteractive getPluginInteractive();

    /**
     * 得到主程序的 ApplicationContext
     * @return MainApplicationContext
     */
    MainApplicationContext getMainApplicationContext();

    /**
     * 得到主程序的 SpringBeanFactory
     * @return SpringBeanFactory
     */
    SpringBeanFactory getMainBeanFactory();

    /**
     * 得到当前框架的集成配置
     * @return IntegrationConfiguration
     */
    IntegrationConfiguration getConfiguration();


    /**
     * 得到当前插件的 ApplicationContext
     * @return GenericApplicationContext
     */
    GenericApplicationContext getApplicationContext();

    /**
     * 得到当前插件的 ClassLoader
     * @return ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * 得到插件的资源loader
     * @return ResourceLoader
     */
    ResourceLoader getResourceLoader();

    /**
     * 获取 WebConfig
     * @return WebConfig
     */
    WebConfig getWebConfig();

    /**
     * set 当前插件的 ApplicationContext
     * @param applicationContext GenericApplicationContext
     */
    void setApplicationContext(GenericApplicationContext applicationContext);

}
