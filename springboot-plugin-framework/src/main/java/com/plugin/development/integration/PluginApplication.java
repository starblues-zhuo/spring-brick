package com.plugin.development.integration;

import com.plugin.development.context.PluginContextListener;
import com.plugin.development.context.PluginSpringBeanListener;
import com.plugin.development.integration.operator.PluginOperator;
import com.plugin.development.integration.user.PluginUser;

/**
 * 插件应用。需要主程序定义成@Bean
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginApplication extends PluginContextListener {


    /**
     * 获得插插件操作者
     * @return 插件操作者
     */
    PluginOperator getPluginOperator();

    /**
     * 获得插件使用者
     * @return 插件使用者
     */
    PluginUser getPluginUser();


}
