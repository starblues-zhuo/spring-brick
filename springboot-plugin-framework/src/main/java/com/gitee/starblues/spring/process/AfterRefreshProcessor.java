package com.gitee.starblues.spring.process;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.OrderPriority;

/**
 * ApplicationContext.refresh 刷新之前的处理扩展接口
 * @author starBlues
 * @version 3.0.0
 */
public interface AfterRefreshProcessor {

    /**
     * 插件注册时调用
     * @param registryInfo 注册插件信息
     * @throws Exception Exception
     */
    void registryOfAfter(SpringPluginRegistryInfo registryInfo) throws Exception;

    /**
     * 插件卸载时调用
     * @param registryInfo 注册插件信息
     * @throws Exception Exception
     */
    default void unRegistryOfAfter(SpringPluginRegistryInfo registryInfo) throws Exception{

    }

    /**
     * 执行顺序 OrderPriority
     * @return OrderPriority
     */
    default OrderPriority orderOfAfter(){
        return OrderPriority.getMiddlePriority();
    }

}
