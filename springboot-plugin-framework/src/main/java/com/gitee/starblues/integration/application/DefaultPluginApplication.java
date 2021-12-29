package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.PluginOperatorWrapper;
import com.gitee.starblues.integration.user.PluginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger LOG = LoggerFactory.getLogger(DefaultPluginApplication.class);

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
        // 检查Configuration
        IntegrationConfiguration configuration = getConfiguration(applicationContext);
        createPluginUser(applicationContext);
        createPluginOperator(applicationContext);
        try {
            if(!(pluginOperator instanceof PluginOperatorWrapper)){
                pluginOperator = new PluginOperatorWrapper(pluginOperator, configuration);
            }
            setBeanFactory(applicationContext);
            pluginOperator.initPlugins(listener);
            beInitialized.set(true);
        } catch (Exception e) {
            LOG.error("初始化插件异常." + e.getMessage());
        }
    }

    /**
     * 创建插件使用者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     */
    protected synchronized PluginUser createPluginUser(ApplicationContext applicationContext){
        if(pluginUser == null){
            pluginUser = applicationContext.getBean(PluginUser.class);
        }
        return pluginUser;
    }

    /**
     * 创建插件操作者。子类可扩展
     * @param applicationContext Spring ApplicationContext
     */
    protected synchronized PluginOperator createPluginOperator(ApplicationContext applicationContext){
        if(pluginOperator == null){
            pluginOperator = applicationContext.getBean(PluginOperator.class);
        }
        return  pluginOperator;
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
