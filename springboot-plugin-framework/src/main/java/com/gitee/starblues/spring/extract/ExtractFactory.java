package com.gitee.starblues.spring.extract;

import java.util.*;

/**
 * 扩展工厂
 * @author starBlues
 * @version 3.0.0
 */
public interface ExtractFactory {

    ExtractFactory INSTANT = new DefaultExtractFactory();

    /**
     * 获取实例
     * @return ExtractFactory
     */
    static ExtractFactory getInstant(){
        return INSTANT;
    }

    /**
     * 通过坐标得到扩展
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    <T> T getExtractByCoordinate(ExtractCoordinate coordinate);

    /**
     * 根据插件id和坐标得到扩展
     * @param pluginId 插件id
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    <T> T getExtractByCoordinate(String pluginId, ExtractCoordinate coordinate);


    /**
     * 根据坐标得到主程序的扩展
     * 主程序扩展必须使用 @Extract+@Component 进行定义
     * @param coordinate 扩展的坐标
     * @param <T> 扩展的泛型
     * @return 扩展实例, 如果不存在则抛出 RuntimeException 异常
     */
    <T> T getExtractByCoordinateOfMain(ExtractCoordinate coordinate);

    /**
     * 根据接口类型获取扩展
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    <T> List<T> getExtractByInterClass(Class<T> interfaceClass);

    /**
     * 根据插件id和接口类型获取扩展
     * @param pluginId 插件id
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    <T> List<T> getExtractByInterClass(String pluginId, Class<T> interfaceClass);

    /**
     * 根据接口类型获取主程序的扩展
     * 主程序扩展必须使用 @Extract+@Component 进行定义
     * @param interfaceClass 接口类类型
     * @param <T> 接口类型泛型
     * @return 扩展实现集合
     */
    <T> List<T> getExtractByInterClassOfMain(Class<T> interfaceClass);

    /**
     * 得到所有的扩展坐标
     * @return 扩展坐标集合, key 为插件id, 值为所有扩展坐标集合
     */
    Map<String, Set<ExtractCoordinate>> getExtractCoordinates();

}
