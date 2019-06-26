package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;
import com.plugin.development.integration.PluginApplication;
import com.plugin.development.integration.operator.PluginOperator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * 自动初始化者
 * @author zhangzhuo
 * @version 1.0
 */
public class AutoPluginInitializer extends AbstractPluginInitializer {


    private final PluginOperator pluginOperator;


    public AutoPluginInitializer(@Autowired PluginApplication pluginApplication) {
        this.pluginOperator = pluginApplication.getPluginOperator();
    }

    public AutoPluginInitializer(PluginApplication pluginApplication,
                                   PluginInitializerListener pluginInitializerListener) {
        super(pluginInitializerListener);
        this.pluginOperator = pluginApplication.getPluginOperator();
    }


    @PostConstruct
    @Override
    public void executeInitialize() throws PluginPlugException {
        try {
            pluginOperator.initPlugins(pluginInitializerListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
