package com.gitee.starblues.spring;

/**
 * 代理工厂
 * @author starBlues
 * @version 3.0.0
 */
public interface ProxyFactory {

    /**
     * 获取代理类
     * @param interfacesClass 需代理的接口
     * @param <T> 代理实现的泛型
     * @return 代理实现
     */
    <T> T getObject(Class<T> interfacesClass);

}
