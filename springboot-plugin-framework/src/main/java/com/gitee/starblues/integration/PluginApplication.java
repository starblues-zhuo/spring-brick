package com.gitee.starblues.integration;

import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.PluginUser;

/**
 * 插件应用。需要主程序定义成@Bean
 * @author zhangzhuo
 * @version 2.0.2
 */
public interface PluginApplication extends PluginListenerContext {


    /**
     * 获得插插件操作者
     * @return 插件操作者
     */
    PluginOperator getPluginOperator();

    /**
     * 获得插插件操作者
     * @return 插件操作者
     */
    PluginUser getPluginUser();

}
