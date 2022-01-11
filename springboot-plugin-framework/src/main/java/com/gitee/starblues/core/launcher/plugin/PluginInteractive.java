package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.extract.OpExtractFactory;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;

/**
 * 插件交互接口
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginInteractive {

    /**
     * 获取插件信息
     * @return PluginDescriptor
     */
    PluginDescriptor getPluginDescriptor();

    /**
     * 获取主程序的 MainApplicationContext
     * @return MainApplicationContext
     */
    MainApplicationContext getMainApplicationContext();

    /**
     * 获取主程序对框架集成配置信息
     * @return IntegrationConfiguration
     */
    IntegrationConfiguration getConfiguration();

    /**
     * 获取远程调用缓存
     * @return InvokeSupperCache
     */
    InvokeSupperCache getInvokeSupperCache();

    /**
     * 获取业务扩展功能的工厂
     * @return OpExtractFactory
     */
    OpExtractFactory getOpExtractFactory();

}
