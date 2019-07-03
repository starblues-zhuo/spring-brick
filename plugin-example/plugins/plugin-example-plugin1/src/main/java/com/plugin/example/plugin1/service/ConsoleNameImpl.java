package com.plugin.example.plugin1.service;

import com.plugin.example.main.config.PluginConfiguration;
import com.plugin.example.main.plugin.ConsoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-06-01 09:30
 * @Update Date Time:
 * @see
 */
@Component
public class ConsoleNameImpl implements ConsoleName {

    @Autowired
    private PluginConfiguration pluginConfiguration;

    @Override
    public String name() {
        return "My name is Plugin1" + "->pluginArgConfiguration :" + pluginConfiguration.toString();
    }
}
