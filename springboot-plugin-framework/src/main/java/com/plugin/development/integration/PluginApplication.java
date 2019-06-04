package com.plugin.development.integration;

import com.plugin.development.context.PluginContextListener;
import com.plugin.development.context.PluginSpringBeanListener;
import com.plugin.development.integration.operator.PluginOperator;
import com.plugin.development.integration.user.PluginUser;

/**
 * @Description: 插件应用。需要主程序定义成@Bean
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 11:07
 * @Update Date Time:
 * @see
 */
public interface PluginApplication extends PluginContextListener {


    /**
     * 获得插件插头
     * @return
     */
    PluginOperator getPluginOperator();

    /**
     * 获得插件使用者
     * @return
     */
    PluginUser getPluginUser();


}
