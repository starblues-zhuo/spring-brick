package com.gitee.starblues.factory.bean.register;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.realize.BasePlugin;

/**
 * 插件bean注册者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginBeanRegister<T> {

    /**
     * 注册者的唯一标识
     * @return String
     */
    String key();


    /**
     * 是否支持注册
     * @param basePlugin 插件信息
     * @param aClass 注册的class
     * @return 支持返回true. 不支持返回false
     */
    boolean support(BasePlugin basePlugin, Class<?> aClass);


    /**
     * 注册插件中的bane
     * @param basePlugin basePlugin
     * @param aClass aClass
     * @return 返回注册的bean的标识。卸载时,会将该参数传入
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    T registry(BasePlugin basePlugin, Class<?> aClass) throws PluginBeanFactoryException;

    /**
     * 卸载插件中的bean
     * @param basePlugin basePlugin
     * @param t 注册时返回的参数
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    void unRegistry(BasePlugin basePlugin, T t) throws PluginBeanFactoryException;

    /**
     * 执行顺序
     * @return 数字越小, 越先执行
     */
    int order();

}
