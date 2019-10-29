package com.gitee.starblues.factory;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private static Map<String, Set<String>> springRegisterBeanNames = new HashMap<>();

    /**
     * 添加注册的bean名称
     * @param pluginId 插件id
     * @param beanName 注册的bean名称
     */
    public static synchronized void addRegisterBeanName(String pluginId, String beanName){
        if(!StringUtils.isEmpty(beanName)){
            Set<String> beanNames = springRegisterBeanNames.get(pluginId);
            if(beanNames == null){
                beanNames = new HashSet<>();
                springRegisterBeanNames.put(pluginId, beanNames);
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
        Set<String> beanNames = springRegisterBeanNames.get(pluginId);
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
        Set<String> beanNames = springRegisterBeanNames.get(pluginId);
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
        for (Set<String> beanNames : springRegisterBeanNames.values()){
            if(beanNames.contains(beanName)){
                return true;
            }
        }
        return false;
    }

}
