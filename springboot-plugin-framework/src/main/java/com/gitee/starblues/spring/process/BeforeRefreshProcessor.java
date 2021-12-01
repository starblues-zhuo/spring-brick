package com.gitee.starblues.spring.process;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.OrderPriority;

/**
 * ApplicationContext.refresh 刷新之后的处理扩展接口
 * @author starBlues
 * @version 3.0.0
 */
public interface BeforeRefreshProcessor {

    /**
     * 插件注册时调用
     * @param registryInfo 注册插件信息
     */
    void registryOfBefore(SpringPluginRegistryInfo registryInfo);

    /**
     * 插件卸载时调用
     * @param registryInfo 注册插件信息
     */
    default void unRegistryOfBefore(SpringPluginRegistryInfo registryInfo){

    }

    /**
     * 执行顺序 OrderPriority
     * @return OrderPriority
     */
    default OrderPriority orderOfBefore(){
        return OrderPriority.getMiddlePriority();
    }

}
