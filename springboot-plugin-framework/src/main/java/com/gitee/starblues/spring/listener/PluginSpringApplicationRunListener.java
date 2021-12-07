package com.gitee.starblues.spring.listener;


import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.Order;
import com.gitee.starblues.utils.OrderPriority;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginSpringApplicationRunListener extends Order {

    default void starting(SpringPluginRegistryInfo registryInfo) throws Exception{
    }

    default void environmentPrepared(SpringPluginRegistryInfo registryInfo) throws Exception{
    }

    default void refreshPrepared(SpringPluginRegistryInfo registryInfo) throws Exception{
    }

    default void started(SpringPluginRegistryInfo registryInfo) throws Exception{
    }

    default void failed(SpringPluginRegistryInfo registryInfo, Throwable exception) throws Exception{
    }

    default void stop(SpringPluginRegistryInfo registryInfo) throws Exception{

    }

    @Override
    default OrderPriority order() {
        return OrderPriority.getMiddlePriority();
    }


    ListenerRunMode runMode();

}

