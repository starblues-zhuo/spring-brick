package com.gitee.starblues.spring.extract;

/**
 * 可操作的扩展工厂
 * @author starBlues
 * @version 3.0.0
 */
public interface OpExtractFactory extends ExtractFactory{

    /**
     * 添加main中的扩展
     * @param extractObject extractObject
     */
    void addOfMain(Object extractObject);

    /**
     * 添加插件中的扩展
     * @param pluginId 插件
     * @param extractObject 扩展对象
     */
    void add(String pluginId, Object extractObject);

    /**
     * 移除插件中的扩展
     * @param pluginId 插件id
     */
    void remove(String pluginId);

}
