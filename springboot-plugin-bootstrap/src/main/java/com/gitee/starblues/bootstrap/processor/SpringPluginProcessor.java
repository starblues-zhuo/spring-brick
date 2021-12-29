package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.utils.Order;
import com.gitee.starblues.utils.OrderPriority;

/**
 * @author starBlues
 * @version 1.0
 */
public interface SpringPluginProcessor extends Order {

    default void initialize(ProcessorContext context) throws ProcessorException{

    }

    default void refreshBefore(ProcessorContext context) throws ProcessorException{

    }

    default void refreshAfter(ProcessorContext context) throws ProcessorException{

    }

    default void failure(ProcessorContext context) throws ProcessorException{

    }

    default void close(ProcessorContext context) throws ProcessorException{

    }

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
