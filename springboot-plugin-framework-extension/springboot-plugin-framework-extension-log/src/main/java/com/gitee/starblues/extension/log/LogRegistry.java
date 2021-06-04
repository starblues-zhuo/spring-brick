package com.gitee.starblues.extension.log;

import com.gitee.starblues.factory.PluginRegistryInfo;
import org.pf4j.PluginWrapper;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 日志注册统一接口
 * @author starBlues
 * @version 2.4.3
 */
public interface LogRegistry {

    /**
     * 注册日志
     * @param resources 日志配置文件资源
     * @param pluginRegistryInfo 当前插件的信息
     * @throws Exception 注册异常
     **/
    void registry(List<Resource> resources, PluginRegistryInfo pluginRegistryInfo) throws Exception;

    /**
     * 注册日志
     * @param pluginRegistryInfo 当前插件的信息
     * @throws Exception 卸载异常
     **/
    void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception;

}
