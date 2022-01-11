package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.PluginListenerContext;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.PluginUser;
import org.springframework.context.ApplicationContext;

/**
 * 插件应用。
 * @author starBlues
 * @version 2.4.3
 */
public interface PluginApplication extends PluginListenerContext {

    /**
     * 初始化
     * @param applicationContext Spring上下文
     * @param listener 插件初始化监听者
     */
    void initialize(ApplicationContext applicationContext, PluginInitializerListener listener);


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
