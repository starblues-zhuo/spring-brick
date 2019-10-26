package com.basic.example.main.config;

import com.gitee.starblues.integration.*;
import com.gitee.starblues.integration.initialize.AutoPluginInitializer;
import com.gitee.starblues.integration.initialize.PluginInitializer;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 插件集成配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Configuration
public class PluginBeanConfig {

    /**
     * 通过默认的集成工厂返回 PluginManager
     * @param integrationConfiguration 集成的配置文件
     * @return
     */
    @Bean
    public PluginManager pluginManager(IntegrationConfiguration integrationConfiguration) {
        IntegrationFactory integrationFactory = new DefaultIntegrationFactory();
        return integrationFactory.getPluginManager(integrationConfiguration);
    }

    /**
     * 定义默认的插件应用。使用可以注入它操作插件。
     * @return
     */
    @Bean
    public PluginApplication pluginApplication(){
        DefaultPluginApplication defaultPluginApplication = new DefaultPluginApplication();
        defaultPluginApplication.addListener(ExamplePluginListener.class);
        return defaultPluginApplication;
    }

    /**
     * 初始化插件。此处定义可以在系统启动时自动加载插件。
     *  如果想手动加载插件, 则可以使用 com.plugin.development.integration.initialize.ManualPluginInitializer 来初始化插件。
     * @param pluginApplication
     * @return
     */
    @Bean
    public PluginInitializer pluginInitializer(PluginApplication pluginApplication,
                                               PluginListener pluginListener){
        AutoPluginInitializer autoPluginInitializer = new AutoPluginInitializer(pluginApplication);
        autoPluginInitializer.setPluginInitializerListener(pluginListener);
        return autoPluginInitializer;
    }
}
