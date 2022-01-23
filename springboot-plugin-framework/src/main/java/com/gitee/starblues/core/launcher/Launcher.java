package com.gitee.starblues.core.launcher;

/**
 * 启动引导器
 * @author starBlues
 * @version 3.0.0
 */
public interface Launcher<R> {

    /**
     * 启动运行
     * @param args 启动传入的参数
     * @return 启动后的返回值
     * @throws Exception 启动异常
     */
    R run(String... args) throws Exception;

}
