package com.gitee.starblues.spring.processor;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.Order;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public interface SpringPluginProcessor extends Order {

    default void init(GenericApplicationContext mainApplicationContext) throws Exception{};

    default void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception{}
    default void refreshAfter(SpringPluginRegistryInfo registryInfo) throws Exception{}

    default void failure(SpringPluginRegistryInfo registryInfo) throws Exception{}

    default void close(SpringPluginRegistryInfo registryInfo) throws Exception{}

    @Override
    default OrderPriority order(){
        return OrderPriority.getLowPriority();
    }


    RunMode runMode();


    enum RunMode{
        /**
         * 全部运行
         */
        ALL,

        /**
         * 插件环境运行
         */
        PLUGIN,

        /**
         * 插件独立运行
         */
        ONESELF
    }

}
