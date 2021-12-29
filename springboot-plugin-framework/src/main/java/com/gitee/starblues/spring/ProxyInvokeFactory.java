package com.gitee.starblues.spring;

/**
 * @author starBlues
 * @version 1.0
 */
public interface ProxyInvokeFactory {

    <T> T getObject(Class<T> interfacesClass);

}
