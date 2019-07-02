package com.gitee.starblues.integration;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.factory.NoticePluginFactory;
import com.gitee.starblues.factory.OverallPluginFactory;
import com.gitee.starblues.factory.PluginListener;
import com.gitee.starblues.factory.PluginFactory;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
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
 * @version 1.0
 */
public class DefaultPluginApplication implements ApplicationContextAware, PluginApplication {

    private final Logger log = LoggerFactory.getLogger(DefaultPluginApplication.class);

    private ApplicationContext applicationContext;
    private PluginManager pluginManager;
    private ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();

    private PluginOperator pluginOperator;
    private NoticePluginFactory noticePluginFactory;

    private List<PluginListener> pluginListeners = new ArrayList<>();

    public DefaultPluginApplication() {
        this(null);
    }


    public DefaultPluginApplication(List<PluginListener> pluginListeners) {
        if (pluginListeners != null && !pluginListeners.isEmpty()) {
            this.pluginListeners.addAll(pluginListeners);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Objects.requireNonNull(applicationContext);
        this.applicationContext = applicationContext;
        this.pluginManager = applicationContext.getBean(PluginManager.class);
        if (this.noticePluginFactory == null) {
            PluginFactory pluginFactory =
                    new OverallPluginFactory(applicationContext);
            this.noticePluginFactory = new NoticePluginFactory(pluginFactory);
            this.noticePluginFactory.addListener(pluginListeners);
        }
        try {
            IntegrationConfiguration configuration = applicationContext.getBean(IntegrationConfiguration.class);
            //this.pluginUser = new DefaultPluginUser(this.applicationContext, this.pluginManager);
            this.pluginOperator = new DefaultPluginOperator(
                    applicationContext,
                    configuration,
                    this.noticePluginFactory,
                    this.pluginManager);
        } catch (Exception e) {
            throw new BeanCreationException("Instant PluginUser or PluginOperator Failure : " + e.getMessage(), e);
        }
    }


    @Override
    public PluginOperator getPluginOperator() {
        assertInjected();
        return pluginOperator;
    }


    @Override
    public void addListener(PluginListener pluginListener) {
        assertInjected();
        if (pluginListener != null) {
            noticePluginFactory.addListener(pluginListener);
        }
    }

    @Override
    public void addListener(List<PluginListener> pluginListeners) {
        assertInjected();
        if (pluginListeners != null) {
            noticePluginFactory.addListener(pluginListeners);
        }
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
}
