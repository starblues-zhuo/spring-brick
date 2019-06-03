package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;
import com.plugin.development.integration.PluginApplication;
import com.plugin.development.integration.operator.PluginOperator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 手动初始化者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 09:55
 * @Update Date Time:
 * @see
 */
public class ManualPluginInitializer extends AbstractPluginInitializer {

    private final PluginOperator pluginOperator;

    @Autowired
    public ManualPluginInitializer(PluginApplication pluginApplication) {
        this.pluginOperator = pluginApplication.getPluginOperator();
    }


    @Override
    public void executeInitialize() throws PluginPlugException {
        pluginOperator.initPlugins();
    }
}
