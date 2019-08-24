package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.utils.OrderPriority;

/**
 * 单插件处理者扩展接口
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public interface PluginPipeProcessorExtend extends PluginPipeProcessor{

    /**
     * 扩展key
     * @return String
     */
    String key();


    /**
     * 执行顺序
     * @return OrderPriority
     */
    OrderPriority order();

}
