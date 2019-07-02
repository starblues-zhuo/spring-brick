package com.plugin.example.plugin2;

import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

/**
 * @Description: 插件定义类
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:00
 * @Update Date Time:
 * @see
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

    @Override
    public String scanPackage() {
        return "com.plugin.example.plugin2";
    }
}
