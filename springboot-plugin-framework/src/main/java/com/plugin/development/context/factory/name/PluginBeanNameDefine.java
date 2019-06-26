package com.plugin.development.context.factory.name;

/**
 * 插件bean name命名
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginBeanNameDefine {

    /**
     * 是否时插件bean name
     * @param beanName bean名称
     * @return boolean
     */
    boolean isPluginBeanName(String beanName);

    /**
     * 得到插件bean名称
     * @param name 名称
     * @return 插件bean名称
     */
    String getPluginName(String name);


}
