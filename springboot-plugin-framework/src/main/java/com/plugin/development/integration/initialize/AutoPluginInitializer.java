package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;
import com.plugin.development.integration.PluginApplication;
import com.plugin.development.integration.operator.PluginOperator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @Description: 初始化者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 09:43
 * @Update Date Time:
 * @see
 */
public class AutoPluginInitializer extends AbstractPluginInitializer {



    private final PluginOperator pluginOperator;

    @Autowired
    public AutoPluginInitializer(PluginApplication pluginApplication) {
        this.pluginOperator = pluginApplication.getPluginOperator();
    }


    @PostConstruct
    @Override
    public void executeInitialize() throws PluginPlugException {
        try {
            pluginOperator.initPlugins();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
