/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
