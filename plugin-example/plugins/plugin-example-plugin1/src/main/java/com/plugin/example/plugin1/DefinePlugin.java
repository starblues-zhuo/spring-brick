package com.plugin.example.plugin1;

import com.plugin.development.realize.BasePlugin;
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
    protected String scanPackage() {
        return "com.plugin.example.plugin1";
    }
}
