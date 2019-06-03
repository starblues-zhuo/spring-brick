package com.plugin.example.plugin1.service;

import com.plugin.development.annotation.ApplyMainBean;
import com.plugin.example.start.config.PluginArgConfiguration;
import com.plugin.example.start.plugin.ConsoleName;
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
@ApplyMainBean
public class ConsoleNameImpl implements ConsoleName {

    @Autowired(required = false)
    private PluginArgConfiguration pluginArgConfiguration;

    @Override
    public String name() {
        return "My name is Plugin1" + "; pluginArgConfiguration :" + pluginArgConfiguration.toString();
    }
}
