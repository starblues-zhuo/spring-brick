package com.plugin.development.context.factory.name;

/**
 * 插件beanName定义工程
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginBeanNameDefineFactory {

    private final static PluginBeanNameDefine PLUGIN_BEAN_NAME_DEFINE = new DefaultPluginBeanNameDefine();

    /**
     * 得到插件beanName定义
     * @return PluginBeanNameDefine
     */
    public static PluginBeanNameDefine get(){
        return PLUGIN_BEAN_NAME_DEFINE;
    }


}
