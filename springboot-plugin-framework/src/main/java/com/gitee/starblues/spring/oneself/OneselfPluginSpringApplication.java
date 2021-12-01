package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.spring.PluginSpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 插件可主运行的 PluginSpringApplication 实现, 插件如果需要自动运行, 则使用该类进行引导启动
 * @author starBlues
 * @version 3.0.0
 */
public class OneselfPluginSpringApplication implements PluginSpringApplication {

    private final OneselfSpringApplication oneselfSpringApplication;

    private ConfigurableApplicationContext applicationContext;

    public OneselfPluginSpringApplication(Class<?> ...primarySources) {
        this.oneselfSpringApplication = new OneselfSpringApplication(this, primarySources);
    }

    /**
     * 插件自主运行时, 引导入口
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
