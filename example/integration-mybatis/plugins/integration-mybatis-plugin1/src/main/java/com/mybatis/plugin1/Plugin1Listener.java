package com.mybatis.plugin1;

import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import com.gitee.starblues.utils.OrderPriority;
import com.mybatis.plugin1.mapper.Plugin1Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
public class Plugin1Listener implements OneselfListener {

    private final Logger logger = LoggerFactory.getLogger(Plugin1Listener.class);

    private final Plugin1Mapper plugin1Mapper;

    public Plugin1Listener(Plugin1Mapper plugin1Mapper) {
        this.plugin1Mapper = plugin1Mapper;
    }


    @Override
    public OrderPriority order() {
        // 定义监听器执行顺序。用于多个监听器
        return OrderPriority.getMiddlePriority();
    }

    @Override
    public void startEvent(BasePlugin basePlugin) {
        // 启动事件
        logger.info("Plugin1Listener {} start Event", basePlugin.getWrapper().getPluginId());
        logger.info("plugin1Mapper getList : {}", plugin1Mapper.getList());
    }

    @Override
    public void stopEvent(BasePlugin basePlugin) {
        // 停止事件
        logger.info("Plugin1Listener {} stop Event", basePlugin.getWrapper().getPluginId());
        logger.info("plugin1Mapper getList : {}", plugin1Mapper.getList());
    }
}
