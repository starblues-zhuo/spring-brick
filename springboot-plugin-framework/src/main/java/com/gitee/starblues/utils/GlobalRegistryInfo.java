package com.gitee.starblues.utils;


import com.gitee.starblues.integration.IntegrationConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局注册信息
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public final class GlobalRegistryInfo {

    private GlobalRegistryInfo(){}

    /**
     * 全局插件安装次数
     */
    private static Map<String, PluginOperatorInfo> operatorPluginInfos = new HashMap<>();

    /**
     * 全局扩展信息
     */
    private static Map<String, Object> extensionMap = new HashMap<>();


    /**
     * 添加操作插件信息
     * @param pluginId 插件id
     * @param operatorType 操作类型
     * @param isLock 是否加锁
     */
    public static synchronized void addOperatorPluginInfo(String pluginId,
                                                          PluginOperatorInfo.OperatorType operatorType,
                                                          boolean isLock){
        PluginOperatorInfo operatorPluginInfo = operatorPluginInfos.get(pluginId);
        if(operatorPluginInfo == null){
            operatorPluginInfo = new PluginOperatorInfo();
            operatorPluginInfos.put(pluginId, operatorPluginInfo);
        }
        operatorPluginInfo.setOperatorType(operatorType);
        operatorPluginInfo.setLock(isLock);
    }


    /**
     * 设置操作插件的信息
     * @param pluginId 插件id
     * @param isLock 是否加锁
     */
    public static synchronized void setOperatorPluginInfo(String pluginId, boolean isLock){
        PluginOperatorInfo operatorPluginInfo = operatorPluginInfos.get(pluginId);
        if(operatorPluginInfo != null){
            operatorPluginInfo.setLock(isLock);
        }
    }



    /**
     * 获取插件安装次数
     * @param pluginId 插件id
     * @return 操作插件类型
     */
    public static synchronized PluginOperatorInfo getPluginInstallNum(String pluginId){
        return operatorPluginInfos.get(pluginId);
    }


    /**
     * 添加全局扩展数据
     * @param key 扩展的key
     * @param value 扩展值
     */
    public static synchronized void addExtension(String key, Object value){
        extensionMap.put(key, value);
    }

    /**
     * 删除全局扩展数据
     * @param key 扩展的key
     */
    public static synchronized void removeExtension(String key){
        extensionMap.remove(key);
    }

    /**
     * 获取全局扩展值
     * @param key 全局扩展的key
     * @param <T> 返回值泛型
     * @return 扩展值
     */
    public static synchronized <T> T getExtension(String key){
        Object o = extensionMap.get(key);
        if(o == null){
            return null;
        } else {
            return (T) o;
        }
    }




}
