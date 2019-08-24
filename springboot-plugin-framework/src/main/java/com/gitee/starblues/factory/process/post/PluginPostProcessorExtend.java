package com.gitee.starblues.factory.process.post;

import com.gitee.starblues.utils.OrderPriority;

/**
 * 后置插件处理者
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public interface PluginPostProcessorExtend extends PluginPostProcessor{

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
