package com.gitee.starblues.spring;

/**
 * 注册信息
 * @author starBlues
 * @version 3.0.0
 */
public interface RegistryInfo {

    /**
     * 添加注册的信息
     * @param key 注册信息key
     * @param value 注册信息值
     */
    void addRegistryInfo(String key, Object value);

    /**
     * 得到注册信息
     * @param key 注册信息key
     * @return 注册信息的值
     */
    <T> T getRegistryInfo(String key);
    /**
     * 移除注册信息
     * @param key 注册信息key
     */
    void removeRegistryInfo(String key);

    /**
     * 清除全部的注册信息
     */
    void clearRegistryInfo();


}
