package com.gitee.starblues.integration;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.integration.listener.PluginListener;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.DefaultPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 开发者直接使用的。插件应用
 * @author zhangzhuo
 * @version 2.0.2
 */
public class DefaultPluginApplication implements ApplicationContextAware, PluginApplication {

    private final Logger log = LoggerFactory.getLogger(DefaultPluginApplication.class);

    private ApplicationContext applicationContext;
    private PluginManager pluginManager;
    private ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();

    private PluginOperator pluginOperator;
    private PluginUser pluginUser;

    private PluginListenerFactory listenerFactory = new PluginListenerFactory();

    public DefaultPluginApplication() {
        this(null);
    }


    public DefaultPluginApplication(List<PluginListener> pluginListeners) {
        addListener(pluginListeners);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Objects.requireNonNull(applicationContext);
        this.applicationContext = applicationContext;
        this.pluginManager = applicationContext.getBean(PluginManager.class);
        try {
            IntegrationConfiguration configuration = applicationContext.getBean(IntegrationConfiguration.class);
            this.pluginUser = new DefaultPluginUser(this.applicationContext, this.pluginManager);
            this.pluginOperator = new DefaultPluginOperator(
                    applicationContext,
                    configuration,
                    this.pluginManager,
                    this.listenerFactory
                    );
        } catch (Exception e) {
            throw new BeanCreationException("Instant PluginUser or PluginOperator Failure : " + e.getMessage(), e);
        }
    }


    @Override
    public PluginOperator getPluginOperator() {
        assertInjected();
        return this.pluginOperator;
    }

    @Override
    public PluginUser getPluginUser() {
        assertInjected();
        return this.pluginUser;
    }

    /**
     * 检查注入
     */
    private void assertInjected() {
        if (this.applicationContext == null) {
            throw new RuntimeException("ApplicationContext is null, Please check whether the DefaultPluginApplication is injected");
        }
        if (this.pluginManager == null) {
            throw new RuntimeException("PluginManager is null, Please check whether the PluginManager is injected");
        }
        if (this.pluginOperator == null) {
            throw new RuntimeException("PluginOperator is null," +
                    " Please check whether the PluginManager or ApplicationContext is injected");
        }
        if (this.pluginUser == null) {
            throw new RuntimeException("pluginUser is null," +
                    " Please check whether the PluginManager or ApplicationContext is injected");
        }
    }

    /**
     * 添加插件扩展
     * @param extension 扩展实现对象
     * @return DefaultPluginApplication
     */
    public DefaultPluginApplication addExtension(AbstractExtension extension) {
        this.extensionFactory.addExtension(extension);
        return this;
    }


    @Override
    public void addListener(PluginListener pluginListener) {
        this.listenerFactory.addPluginListener(pluginListener);
    }

    @Override
    public void addListener(List<PluginListener> pluginListeners) {
        if(pluginListeners == null || pluginListeners.isEmpty()){
            return;
        }
        for (PluginListener pluginListener : pluginListeners) {
            this.listenerFactory.addPluginListener(pluginListener);
        }
    }
}
