package com.basic.example.main.plugin;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.refresh.AbstractPluginSpringBeanRefresh;
import org.springframework.stereotype.Component;

/**
 * 输出名称工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class ConsoleNameFactory extends AbstractPluginSpringBeanRefresh<ConsoleName> {
    public ConsoleNameFactory(PluginApplication pluginApplication) {
        super(pluginApplication);
        pluginApplication.addListener(this);
    }
}
