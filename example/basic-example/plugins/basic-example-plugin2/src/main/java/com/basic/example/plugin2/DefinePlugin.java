package com.basic.example.plugin2;

import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

/**
 * 插件定义类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefinePlugin extends BasePlugin {
    public DefinePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected void startEvent() throws PluginException {

    }

    @Override
    protected void deleteEvent() throws PluginException {

    }

    @Override
    protected void stopEvent() {

    }
}
