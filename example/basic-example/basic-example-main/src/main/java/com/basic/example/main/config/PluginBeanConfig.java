package com.basic.example.main.config;

import com.gitee.starblues.extension.support.SpringDocControllerProcessor;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.application.AutoPluginApplication;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.quartz.SchedulerFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 插件集成配置
 *
 * @author starBlues
 * @version 1.0
 */
@Configuration
public class PluginBeanConfig {


    /**
     * 定义插件应用。使用可以注入它操作插件。
     * @return PluginApplication
     */
    @Bean
    public PluginApplication pluginApplication(PluginListener pluginListener,
                                               SchedulerFactory schedulerFactory){
        AutoPluginApplication autoPluginApplication = new AutoPluginApplication();
        autoPluginApplication.setPluginInitializerListener(pluginListener);
        autoPluginApplication.addListener(ExamplePluginListener.class);
        return autoPluginApplication;
    }

    /**
     * 集成 SpringDoc 的插件刷新
     * @param applicationContext ApplicationContext
     * @return SpringDocControllerProcessor
     */
    @Bean
    public SpringDocControllerProcessor springDocControllerProcessor(ApplicationContext applicationContext){
        return new SpringDocControllerProcessor(applicationContext);
    }

}
