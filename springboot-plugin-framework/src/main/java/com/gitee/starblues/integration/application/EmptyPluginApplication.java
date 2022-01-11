package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.operator.EmptyPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.EmptyPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class EmptyPluginApplication implements PluginApplication{


    @Override
    public void initialize(ApplicationContext applicationContext, PluginInitializerListener listener) {

    }

    @Override
    public PluginOperator getPluginOperator() {
        return new EmptyPluginOperator();
    }

    @Override
    public PluginUser getPluginUser() {
        return new EmptyPluginUser();
    }

    @Override
    public void addListener(PluginListener pluginListener) {

    }

    @Override
    public <T extends PluginListener> void addListener(Class<T> pluginListenerClass) {

    }

    @Override
    public void addListener(List<PluginListener> pluginListeners) {

    }
}
