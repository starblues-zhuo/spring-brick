package com.basic.example.plugin1.service;

import com.basic.example.main.config.PluginConfiguration;
import com.basic.example.main.plugin.ConsoleName;
import com.gitee.starblues.realize.PluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 主程序定义接口的实现类: ConsoleName
 *
 * @author starBlues
 * @version 1.0
 */
@Component
public class ConsoleNameImpl implements ConsoleName {

    @Autowired
    private PluginUtils pluginUtils;

    @Override
    public String name() {
        PluginConfiguration pluginConfiguration = pluginUtils.getMainBean(PluginConfiguration.class);
        return "My name is Plugin1" + "->pluginArgConfiguration :" + pluginConfiguration.toString();
    }
}
