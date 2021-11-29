package com.gitee.starblues.integration.application;

import com.gitee.starblues.core.DefaultRealizeProvider;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.integration.operator.PluginOperatorWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.DefaultPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.spring.DefaultSpringPlugin;
import com.gitee.starblues.spring.SpringPlugin;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * 默认的插件 PluginApplication
 * @author starBlues
 * @version 2.4.4
 */
public class DefaultPluginApplication extends AbstractPluginApplication {


    private PluginUser pluginUser;
    private PluginOperator pluginOperator;

    private final AtomicBoolean beInitialized = new AtomicBoolean(false);

    public DefaultPluginApplication() {
    }

    @Override
    public synchronized void initialize(ApplicationContext applicationContext,
                                        PluginInitializerListener listener) {
        Objects.requireNonNull(applicationContext, "ApplicationContext can't be null");
        if(beInitialized.get()){
            throw new RuntimeException("Plugin has been initialized");
        }
        IntegrationConfiguration configuration = getConfiguration(applicationContext);
        pluginUser = createPluginUser(applicationContext);
        pluginOperator = createPluginOperator(applicationContext, configuration);
        try {
            setBeanFactory(applicationContext);
            pluginOperator.initPlugins(listener);
            beInitialized.set(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建插件使用者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     * @return PluginUser
     */
    protected PluginUser createPluginUser(ApplicationContext applicationContext){
        return new DefaultPluginUser(applicationContext);
    }

    /**
     * 创建插件操作者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     * @param configuration 当前集成的配置
     * @return PluginOperator
     */
    protected PluginOperator createPluginOperator(ApplicationContext applicationContext,
                                                  IntegrationConfiguration configuration){
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        PluginOperator pluginOperator = new DefaultPluginOperator(
                genericApplicationContext,
                createSpringPlugin(genericApplicationContext),
                createRealizeProvider(configuration),
                configuration
        );
        return new PluginOperatorWrapper(pluginOperator, configuration);
    }

    protected SpringPlugin createSpringPlugin(GenericApplicationContext applicationContext){
        return new DefaultSpringPlugin(applicationContext);
    }

    protected RealizeProvider createRealizeProvider(IntegrationConfiguration configuration){
        return new DefaultRealizeProvider(configuration.environment(), configuration.mainPackage());
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

//    /**
//     * 将pf4j中的监听器加入
//     * @param pluginManager pluginManager
//     * @param applicationContext ApplicationContext
//     */
//    private void addPf4jStateListener(PluginManager pluginManager, ApplicationContext applicationContext){
//        List<PluginStateListener> pluginStateListeners = pluginStateListenerFactory
//                .buildListenerClass((GenericApplicationContext) applicationContext);
//        if(ObjectUtils.isEmpty(pluginStateListeners)){
//            return;
//        }
//        for (PluginStateListener pluginStateListener : pluginStateListeners) {
//            pluginManager.addPluginStateListener(pluginStateListener);
//        }
//    }


    /**
     * 直接将 PluginOperator 和 PluginUser 注入到ApplicationContext容器中
     * @param applicationContext ApplicationContext
     */
    private void setBeanFactory(ApplicationContext applicationContext){
        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory = genericApplicationContext.getDefaultListableBeanFactory();
        defaultListableBeanFactory.registerSingleton(pluginOperator.getClass().getName(), pluginOperator);
        defaultListableBeanFactory.registerSingleton(pluginUser.getClass().getName(), pluginUser);
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
