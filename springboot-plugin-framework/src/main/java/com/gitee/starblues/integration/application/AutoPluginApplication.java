package com.gitee.starblues.integration.application;

import com.gitee.starblues.integration.pf4j.Pf4jFactory;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 自动初始化的 PluginApplication。该PluginApplication 基于 Spring InitializingBean 自动初始化插件。
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class AutoPluginApplication extends DefaultPluginApplication
        implements PluginApplication, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private PluginInitializerListener pluginInitializerListener;

    public AutoPluginApplication() {
        super();
    }

    public AutoPluginApplication(Pf4jFactory pf4jFactory) {
        super(pf4jFactory);
    }

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
     * @throws Exception 初始化异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        if(applicationContext == null){
            throw new Exception("Auto initialize failed. ApplicationContext Not injected.");
        }
        super.initialize(applicationContext, pluginInitializerListener);
    }
}
