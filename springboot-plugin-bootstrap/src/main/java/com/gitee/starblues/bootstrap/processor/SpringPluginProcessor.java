package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.utils.Order;
import com.gitee.starblues.utils.OrderPriority;

/**
 * spring plugin 处理器
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPluginProcessor extends Order {

    /**
     * 初始化时
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void initialize(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 刷新上下文前
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void refreshBefore(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 刷新上下文后
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void refreshAfter(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 启动失败
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void failure(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 关闭容器时
     * @param context ProcessorContext
     * @throws ProcessorException 处理异常
     */
    default void close(ProcessorContext context) throws ProcessorException{

    }

    /**
     * 执行顺序
     * @return OrderPriority
     */
    @Override
    default OrderPriority order(){
        return OrderPriority.getLowPriority();
    }

    /**
     * 处理器运行模式
     * @return RunMode
     */
    RunMode runMode();

    /**
     * 运行模式
     */
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
