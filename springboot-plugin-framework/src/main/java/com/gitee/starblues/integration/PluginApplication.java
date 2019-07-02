package com.gitee.starblues.integration;

import com.gitee.starblues.factory.PluginListenerContext;
import com.gitee.starblues.integration.operator.PluginOperator;

/**
 * 插件应用。需要主程序定义成@Bean
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginApplication extends PluginListenerContext {


    /**
     * 获得插插件操作者
     * @return 插件操作者
     */
    PluginOperator getPluginOperator();



}
