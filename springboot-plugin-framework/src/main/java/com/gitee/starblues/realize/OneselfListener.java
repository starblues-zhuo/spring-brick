package com.gitee.starblues.realize;

import com.gitee.starblues.utils.OrderPriority;

/**
 * 监听本插件模块事件的监听者接口
 *
 * @author zhangzhuo
 * @version 2.2.1
 */
public interface OneselfListener {

    /**
     * 执行优先级。用于多个监听器的时候
     * @return OrderPriority
     */
    OrderPriority order();


    /**
     * 启动事件
     * @param basePlugin 当前插件实现的BasePlugin类
     */
    void startEvent(BasePlugin basePlugin);


    /**
     * 停止事件
     * @param basePlugin 当前插件实现的BasePlugin类
     */
    void stopEvent(BasePlugin basePlugin);

}
