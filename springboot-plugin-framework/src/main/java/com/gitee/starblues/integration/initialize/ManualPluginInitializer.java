package com.gitee.starblues.integration.initialize;

import com.gitee.starblues.exception.PluginPlugException;
import com.gitee.starblues.integration.PluginApplication;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;

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

    public ManualPluginInitializer(PluginApplication pluginApplication,
                                   PluginInitializerListener pluginInitializerListener) {
        super(pluginInitializerListener);
        this.pluginOperator = pluginApplication.getPluginOperator();
    }


    @Override
    public void executeInitialize() throws PluginPlugException {
        pluginOperator.initPlugins(pluginInitializerListener);
    }

}
