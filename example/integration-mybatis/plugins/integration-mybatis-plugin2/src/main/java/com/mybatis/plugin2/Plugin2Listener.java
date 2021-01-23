package com.mybatis.plugin2;

import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import com.gitee.starblues.realize.PluginUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.mybatis.main.mapper.RoleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
public class Plugin2Listener implements OneselfListener {

    private final Logger logger = LoggerFactory.getLogger(Plugin2Listener.class);

    private final RoleMapper roleMapper;

    @Autowired
    public Plugin2Listener(PluginUtils pluginUtils){
        roleMapper = pluginUtils.getMainBean(RoleMapper.class);
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getMiddlePriority();
    }

    @Override
    public void startEvent(BasePlugin basePlugin) {
        logger.info("Plugin2Listener {} start Event", basePlugin.getWrapper().getPluginId());
        logger.info("RoleMapper getList : {}", roleMapper.getList());
    }

    @Override
    public void stopEvent(BasePlugin basePlugin) {
        logger.info("Plugin2Listener {} stop Event", basePlugin.getWrapper().getPluginId());
        logger.info("RoleMapper getList : {}", roleMapper.getList());
    }
}
