package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.spring.PluginSpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class OneselfPluginSpringApplication implements PluginSpringApplication {

    private final OneselfSpringApplication oneselfSpringApplication;

    private ConfigurableApplicationContext applicationContext;

    public OneselfPluginSpringApplication(Class<?> ...primarySources) {
        this.oneselfSpringApplication = new OneselfSpringApplication(this, primarySources);
    }

    /**
     * 插件自主调用
     * @param primarySources primarySources
     * @return ConfigurableApplicationContext
     */
    public static ConfigurableApplicationContext run(Class<?> ...primarySources){
        return new OneselfPluginSpringApplication(primarySources).run();
    }


    @Override
    public ConfigurableApplicationContext run() {
        applicationContext = oneselfSpringApplication.run();
        return this.applicationContext;
    }

    @Override
    public void close() {
        applicationContext.close();
    }

    @Override
    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
