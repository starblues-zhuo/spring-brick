package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.operator.PluginOperatorWrapper;
import com.gitee.starblues.integration.pf4j.DefaultPf4jFactory;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.pf4j.Pf4jFactory;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.DefaultPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 默认的插件 PluginApplication
 * @author zhangzhuo
 * @version 2.3.1
 */
public class DefaultPluginApplication extends AbstractPluginApplication {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected Pf4jFactory integrationFactory;

    private PluginUser pluginUser;
    private PluginOperator pluginOperator;

    private AtomicBoolean beInitialized = new AtomicBoolean(false);

    public DefaultPluginApplication() {
    }

    public DefaultPluginApplication(Pf4jFactory integrationFactory){
        this.integrationFactory = integrationFactory;
    }


    @Override
    public synchronized void initialize(ApplicationContext applicationContext,
                                        PluginInitializerListener listener) {
        Objects.requireNonNull(applicationContext, "ApplicationContext can't be null");
        if(beInitialized.get()){
            throw new RuntimeException("Plugin has been initialized");
        }
        IntegrationConfiguration configuration = getConfiguration(applicationContext);
        if(integrationFactory == null){
            integrationFactory = new DefaultPf4jFactory(configuration);
        }
        PluginManager pluginManager = integrationFactory.getPluginManager();
        pluginUser = createPluginUser(applicationContext, pluginManager);
        pluginOperator = createPluginOperator(applicationContext, pluginManager, configuration);
        try {
            pluginOperator.initPlugins(listener);
            beInitialized.set(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建插件使用者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     * @param pluginManager 插件管理器
     * @return PluginUser
     */
    protected PluginUser createPluginUser(ApplicationContext applicationContext,
                                          PluginManager pluginManager){
        return new DefaultPluginUser(applicationContext, pluginManager);
    }

    /**
     * 创建插件操作者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     * @param pluginManager 插件管理器
     * @param configuration 当前集成的配置
     * @return PluginOperator
     */
    protected PluginOperator createPluginOperator(ApplicationContext applicationContext,
                                                  PluginManager pluginManager,
                                                  IntegrationConfiguration configuration){
        PluginOperator pluginOperator = new DefaultPluginOperator(
                applicationContext,
                configuration,
                pluginManager,
                this.listenerFactory
        );
        return new PluginOperatorWrapper(pluginOperator, configuration);
    }


    @Override
    public PluginOperator getPluginOperator() {
        assertInjected();
        return pluginOperator;
    }

    @Override
    public PluginUser getPluginUser() {
        assertInjected();
        return pluginUser;
    }

    /**
     * 检查注入
     */
    private void assertInjected() {
        if (this.pluginUser == null) {
            throw new RuntimeException("PluginUser is null, Please check whether the DefaultPluginApplication is injected");
        }
        if (this.pluginOperator == null) {
            throw new RuntimeException("PluginOperator is null, Please check whether the DefaultPluginApplication is injected");
        }
    }


}
