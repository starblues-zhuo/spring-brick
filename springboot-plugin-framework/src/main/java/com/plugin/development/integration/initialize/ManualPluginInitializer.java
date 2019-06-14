package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;
import com.plugin.development.integration.PluginApplication;
import com.plugin.development.integration.operator.PluginOperator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 插件手动初始化者
 * @author zhangzhuo
 * @version 1.0
 */
public class ManualPluginInitializer extends AbstractPluginInitializer {

    private final PluginOperator pluginOperator;

    public ManualPluginInitializer(PluginApplication pluginApplication) {
        this.pluginOperator = pluginApplication.getPluginOperator();
    }


    @Override
    public void executeInitialize() throws PluginPlugException {
        pluginOperator.initPlugins();
    }
}
