package com.gitee.starblues.register;

import org.pf4j.PluginWrapper;

/**
 * 注册监听者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface RegisterListener {

    /**
     * 操作成功
     * @param pluginWrapper 插件
     */
    void success(PluginWrapper pluginWrapper);

    /**
     * 操作失败
     * @param pluginWrapper 插件
     * @param throwable 失败异常信息
     */
    void failure(PluginWrapper pluginWrapper, Throwable throwable);

}
