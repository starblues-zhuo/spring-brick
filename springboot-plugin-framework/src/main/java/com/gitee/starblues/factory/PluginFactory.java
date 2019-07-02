package com.gitee.starblues.factory;

import com.gitee.starblues.exception.PluginFactoryException;
import org.pf4j.PluginWrapper;

/**
 * 插件工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginFactory{

    /**
     * 工厂类型
     * @return FactoryType
     */
    FactoryType factoryType();

    /**
     * 注册插件中的bane
     * @param pluginWrapper pluginWrapper
     * @throws PluginFactoryException 插件工厂异常
     */
    void registry(PluginWrapper pluginWrapper) throws PluginFactoryException;

    /**
     * 卸载插件中的bean
     * @param pluginWrapper 插件id
     * @throws PluginFactoryException 插件工厂异常
     */
    void unRegistry(PluginWrapper pluginWrapper) throws PluginFactoryException;

    /**
     * 执行顺序
     * @return 数字越小, 越先执行
     */
    int order();


}
