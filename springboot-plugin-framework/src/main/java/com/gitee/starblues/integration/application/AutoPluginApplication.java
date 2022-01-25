package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.ExtendPointConfiguration;
import com.gitee.starblues.integration.ExtendPointWebConfiguration;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.PluginUser;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Import;

/**
 * 自动初始化的 PluginApplication。该PluginApplication 基于 Spring InitializingBean 自动初始化插件。
 *
 * @author starBlues
 * @version 2.2.0
 */
@Import({ExtendPointConfiguration.class, ExtendPointWebConfiguration.class})
public class AutoPluginApplication extends DefaultPluginApplication
        implements PluginApplication, ApplicationContextAware, ApplicationListener<ApplicationStartedEvent> {

    private ApplicationContext applicationContext;
    private PluginInitializerListener pluginInitializerListener;


    /**
     * 设置插件初始化监听器
     * @param pluginInitializerListener 插件监听器
     */
    public void setPluginInitializerListener(PluginInitializerListener pluginInitializerListener) {
        this.pluginInitializerListener = pluginInitializerListener;
    }


    @Override
    public void initialize(ApplicationContext applicationContext,
                           PluginInitializerListener listener) {
        // 此处不允许手动初始化！
        throw new RuntimeException("Cannot be initialized manually");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Spring boot bean属性被Set完后调用。会自动初始化插件
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        super.initialize(applicationContext, pluginInitializerListener);
    }

    @Override
    public PluginOperator getPluginOperator() {
        return createPluginOperator(applicationContext);
    }

    @Override
    public PluginUser getPluginUser() {
        return createPluginUser(applicationContext);
    }
}
