package com.plugin.development.realize;

import org.springframework.context.ApplicationContext;

/**
 * @Description: 插件的 ApplicationContext
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 08:54
 * @Update Date Time:
 * @see
 */
public interface PluginApplicationContext {

    /**
     * 得到 applicationContext
     * @return
     */
    ApplicationContext getApplicationContext();

}
