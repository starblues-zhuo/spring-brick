package com.gitee.starblues.integration.initialize;

import com.gitee.starblues.exception.PluginPlugException;
import com.gitee.starblues.integration.PluginApplication;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
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
