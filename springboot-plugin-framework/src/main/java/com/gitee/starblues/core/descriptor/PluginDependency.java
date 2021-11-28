package com.gitee.starblues.core.descriptor;

/**
 * 插件依赖信息
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginDependency {


    /**
     * 依赖插件id
     * @return String
     */
    String getDependencyId();

    /**
     * 依赖插件版本
     * @return String
     */
    String getDependencyVersion();

    /**
     * 是否必须的
     * @return boolean
     */
    boolean optional();


}
