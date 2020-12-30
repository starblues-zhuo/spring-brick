package com.gitee.starblues.factory;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件信息共享容器
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginInfoContainer {

    private PluginInfoContainer(){}


    /**
     * 全局插件中定义的BaneName
     */
    private final static Map<String, Set<String>> REGISTER_BEAN_NAMES = new ConcurrentHashMap<>();

    /**
     * 添加注册的bean名称
     * @param pluginId 插件id
     * @param beanName 注册的bean名称
     */
    public static synchronized void addRegisterBeanName(String pluginId, String beanName){
        if(!StringUtils.isEmpty(beanName)){
            Set<String> beanNames = REGISTER_BEAN_NAMES.get(pluginId);
            if(beanNames == null){
                beanNames = new HashSet<>();
                REGISTER_BEAN_NAMES.put(pluginId, beanNames);
            }
            beanNames.add(beanName);
        }
    }

    /**
     * 移除注册的bean名称
     * @param pluginId 插件id
     * @param beanName 注册的bean名称
     */
    public static synchronized void removeRegisterBeanName(String pluginId, String beanName){
        Set<String> beanNames = REGISTER_BEAN_NAMES.get(pluginId);
        if(beanNames != null){
            beanNames.remove(beanName);
        }
    }

    /**
     * 是否存在bean名称
     * @param pluginId 插件id
     * @param beanName 注册的bean名称
     * @return true 存在。false不存在
     */
    public static synchronized boolean existRegisterBeanName(String pluginId, String beanName){
        Set<String> beanNames = REGISTER_BEAN_NAMES.get(pluginId);
        if(beanNames != null){
            return beanNames.contains(beanName);
        } else {
            return false;
        }
    }

    /**
     * 是否存在bean名称
     * @param beanName 注册的bean名称
     * @return true 存在。false不存在
     */
    public static synchronized boolean existRegisterBeanName(String beanName){
        for (Set<String> beanNames : REGISTER_BEAN_NAMES.values()){
            if(beanNames.contains(beanName)){
                return true;
            }
        }
        return false;
    }

}
