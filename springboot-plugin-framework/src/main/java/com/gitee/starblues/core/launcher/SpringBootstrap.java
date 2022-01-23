package com.gitee.starblues.core.launcher;

/**
 * 主程序实现该接口引导启动SpringBoot
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringBootstrap {
    /**
     * 启动
     * @param args 启动参数
     * @throws Exception 启动异常
     */
    void run(String[] args) throws Exception;

}
