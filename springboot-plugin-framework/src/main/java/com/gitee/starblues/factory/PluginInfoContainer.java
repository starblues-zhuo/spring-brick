package com.gitee.starblues.factory;

import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 插件信息共享容器
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginInfoContainer {

    private PluginInfoContainer(){}


    /**
     * 全局插件中定义的BaneName
     */
    private static Set<String> springRegisterBeanNames = new HashSet<>();


    public static synchronized void addRegisterBeanName(String beanName){
        if(!StringUtils.isEmpty(beanName)){
            springRegisterBeanNames.add(beanName);
        }
    }

    public static synchronized void removeRegisterBeanName(String beanName){
        springRegisterBeanNames.remove(beanName);
    }


    public static synchronized boolean existRegisterBeanName(String beanName){
        return springRegisterBeanNames.contains(beanName);
    }


}
