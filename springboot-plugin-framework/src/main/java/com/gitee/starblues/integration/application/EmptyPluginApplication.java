package com.gitee.starblues.integration.application;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.operator.EmptyPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.EmptyPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import org.pf4j.PluginStateListener;
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
    public PluginApplication addExtension(AbstractExtension extension) {
        return null;
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

    @Override
    public void addPf4jStateListener(PluginStateListener pluginListener) {

    }

    @Override
    public <T extends PluginStateListener> void addPf4jStateListener(Class<T> pluginListenerClass) {

    }

    @Override
    public void addPf4jStateListener(List<PluginStateListener> pluginListeners) {

    }
}
