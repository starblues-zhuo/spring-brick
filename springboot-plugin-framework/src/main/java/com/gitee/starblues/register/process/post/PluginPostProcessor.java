package com.gitee.starblues.register.process.post;

import com.gitee.starblues.register.FailureNotifier;
import com.gitee.starblues.register.RegisterListener;
import com.gitee.starblues.register.PluginRegistryInfo;

import java.util.List;

/**
 * 插件后置处理者接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginPostProcessor {


    /**
     * 处理该插件的注册
     * @param pluginRegistryInfos 插件注册的信息
     * @throws Exception 处理异常
     */
    void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception;


    /**
     * 处理该插件的卸载
     * @param pluginRegistryInfos 插件注册的信息
     * @throws Exception 处理异常
     */
    void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception;



}
