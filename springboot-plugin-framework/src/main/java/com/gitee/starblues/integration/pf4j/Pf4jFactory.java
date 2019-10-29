package com.gitee.starblues.integration.pf4j;

import com.gitee.starblues.integration.application.DefaultPluginApplication;
import org.pf4j.PluginManager;

/**
 * Pf4j 集成工厂。获取Pf4j的PluginManager对象
 * @author zhangzhuo
 * @version 2.2.0
 * @see DefaultPluginApplication
 */
public interface Pf4jFactory {

    /**
     * 得到插件管理者
     * @return 插件管理者
     */
    PluginManager getPluginManager();

}
