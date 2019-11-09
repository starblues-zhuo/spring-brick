package com.basic.example.main.config;

import com.gitee.starblues.integration.application.DefaultPluginApplication;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.application.AutoPluginApplication;
import org.springframework.context.ApplicationContext;
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
     * 定义插件应用。使用可以注入它操作插件。
     * @return PluginApplication
     */
    @Bean
    public PluginApplication pluginApplication(PluginListener pluginListener){
        AutoPluginApplication autoPluginApplication = new AutoPluginApplication();
        autoPluginApplication.setPluginInitializerListener(pluginListener);
        autoPluginApplication.addListener(ExamplePluginListener.class);
        return autoPluginApplication;
    }

}
