package com.plugin.development.integration.user;

import java.util.List;
import java.util.Map;

/**
 * 插件使用者
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginUser {

    /**
     * 通过bean名称得到插件的bean。（Spring管理的bean）
     * @param name 插件bean的名称。spring体系中的bean名称。可以通过注解定义，也可以自定义生成。具体可百度
     * @param <T> bean的类型
     * @return T
     */
    <T> T getSpringDefineBean(String name);

    /**
     * 在主程序中定义的接口。插件或者主程序实现该接口。可以该方法获取到实现该接口的所有实现类。（Spring管理的bean）
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return List
     */
    <T> List<T> getSpringDefineBeansOfType(Class<T> aClass);


    /**
     * 在主程序中定义的接口。获取插件中实现该接口的实现类。（Spring管理的bean）
     * @param aClass 接口的类
     * @param <T> bean的类型
     * @return Map
     */
    <T> List<T> getPluginSpringDefineBeansOfType(Class<T> aClass);

    /**
     * 在主程序中获取注入了插件实现接口的bean。（Spring管理的bean）
     * @param aClass bean的类型
     * @param <T> bean的类型
     * @return T
     */

    <T> T getSpringAutowirePluginDefineBean(Class<T> aClass);

    /**
     * 得到插件扩展接口实现的bean。（非Spring管理）
     * @param tClass bean的类型
     * @param <T> bean的类型
     * @return List
     */
    <T> List<T> getPluginExtensions(Class<T> tClass);

}
