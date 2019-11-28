package com.gitee.starblues.realize;

/**
 * 插件可配置自定义bean的接口。
 * 注意：该实现类只能注入插件中的配置文件和主程序bean. 不能注入插件中其他的组件bean。
 * bean 指的是Spring 容器中管理的bean
 *
 * @author zhangzhuo
 * @version 2.2.2
 */
public interface ConfigBean {


    /**
     * 初始化。所有bean的初始化工作在此处实现
     * @throws Exception 初始化异常
     */
    void initialize() throws Exception;

    /**
     * 销毁实现
     * @throws Exception 销毁异常
     */
    void destroy() throws Exception;

}
