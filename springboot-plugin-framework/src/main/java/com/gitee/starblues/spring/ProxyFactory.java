package com.gitee.starblues.spring;

/**
 * 代理工厂
 * @author starBlues
 * @version 3.0.0
 */
public interface ProxyFactory {

    <T> T getObject(Class<T> interfacesClass);

}
