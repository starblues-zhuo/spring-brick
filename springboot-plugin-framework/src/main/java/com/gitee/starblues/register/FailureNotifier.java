package com.gitee.starblues.register;

import org.pf4j.PluginWrapper;

/**
 * 插件处理失败通知
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public interface FailureNotifier {

    /**
     * 注册插件。
     * @param pluginWrapper 插件集合
     * @param throwable 异常信息
     */
    void notice(PluginWrapper pluginWrapper, Throwable throwable);

    /**
     * 是否发现错误
     * @return 发现错误返回true.没有发现错误返回false
     */
    boolean foundFailure();

}
