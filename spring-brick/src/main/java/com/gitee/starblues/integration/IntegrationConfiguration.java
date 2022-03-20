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

package com.gitee.starblues.integration;


import com.gitee.starblues.common.Constants;
import com.gitee.starblues.core.RuntimeMode;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.http.CacheControl;

import java.util.List;
import java.util.Set;


/**
 * 插件集成时的配置接口。插件集成的配置接口
 * @author starBlues
 * @version 3.0.0
 */
public interface IntegrationConfiguration {

    /**
     * 是否启用该插件框架
     * @return true 启用, false 禁用
     */
    boolean enable();

    /**
     * 运行环境。运行项目时的模式。分为开发环境(Dev)、生产环境(Prod)
     * @return RuntimeMode.DEV、RuntimeMode.PROD
     */
    RuntimeMode environment();

    /**
     * 主程序包名
     * @return String
     */
    String mainPackage();

    /**
     * 插件的路径。可设置多个插件路径
     * 开发环境建议直接配置为插件模块的父级目录。例如: plugins。如果启动主程序时, 插件为加载, 请检查该配置是否正确。
     * @return 插件的路径
     */
    List<String> pluginPath();

    /**
     * 上传插件包存储的临时路径。默认 temp(相对于主程序jar路径)。
     * @return 上传插件的临时保存路径。
     */
    String uploadTempPath();

    /**
     * 插件备份路径。默认 backupPlugin  (相对于主程序jar路径)。
     * @return 插件备份路径。
     */
    String backupPath();

    /**
     * 插件 RestController 统一请求的路径前缀
     * @return path
     */
    String pluginRestPathPrefix();

    /**
     * 是否启用插件id作为RestController的路径前缀。
     * 如果启用。则路径前缀为 pluginRestPathPrefix() 返回的路径拼接插件id,
     * 即为: /pathPrefix/pluginId/**
     * @return boolean
     */
    boolean enablePluginIdRestPathPrefix();

    /**
     * 启用的插件id
     * @return Set
     */
    Set<String> enablePluginIds();

    /**
     * 禁用的插件id, 禁用后系统不会启动该插件
     * 如果禁用所有插件, 则Set集合中返回一个字符: *
     * @return Set
     */
    Set<String> disablePluginIds();

    /**
     * 设置初始化时插件启动的顺序.
     * @return 有顺序的插件id
     */
    List<String> sortInitPluginIds();

    /**
     * 当前主程序的版本号, 用于校验插件是否可安装.
     * 插件中可通过插件配置信息 requires 来指定可安装的主程序版本
     * @return 系统版本号, 如果为为空或者 0.0.0 表示不校验
     */
    String version();

    /**
     * 设置为true表示插件设置的requires的版本号完全匹配version版本号才可允许插件安装, 即: [requires]=[x.y.z]
     * 设置为false表示插件设置的requires的版本号小于等于version值, 插件就可安装, 即[requires]小于等于[x.y.z]
     * 默认为false
     * @return true or false
     */
    boolean exactVersion();


    /**
     * 检查配置
     */
    default void checkConfig(){};


    /**
     * 是否是开发环境
     * @return boolean
     */
    default boolean isDev(){
        return environment() == RuntimeMode.DEV;
    }

    /**
     * 是否是生产环境
     * @return boolean
     */
    default boolean isProd(){
        return environment() == RuntimeMode.PROD;
    }


    /**
     * 是否被启动
     * @param pluginId 插件id
     * @return true: 启用, false: 未启用
     */
    default boolean isEnable(String pluginId){
        if(ObjectUtils.isEmpty(enablePluginIds())){
            return true;
        }
        if(isDisabled(pluginId)){
            return false;
        }
        return enablePluginIds().contains(pluginId);
    }


    /**
     * 是否被禁用
     * @param pluginId 插件id
     * @return true: 禁用, false: 未禁用
     */
    default boolean isDisabled(String pluginId){
        if(ObjectUtils.isEmpty(disablePluginIds())){
            return false;
        }
        if(disablePluginIds().contains(Constants.DISABLED_ALL_PLUGIN)){
            return true;
        }
        return disablePluginIds().contains(pluginId);
    }

}
