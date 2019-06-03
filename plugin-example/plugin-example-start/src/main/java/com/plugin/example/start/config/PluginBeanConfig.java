package com.plugin.example.start.config;

import com.plugin.development.integration.DefaultIntegrationFactory;
import com.plugin.development.integration.DefaultPluginApplication;
import com.plugin.development.integration.IntegrationFactory;
import com.plugin.development.integration.PluginApplication;
import com.plugin.development.integration.initialize.AutoPluginInitializer;
import com.plugin.development.integration.initialize.PluginInitializer;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 15:53
 * @Update Date Time:
 * @see
 */
@Configuration
public class PluginBeanConfig {

    @Bean
    public PluginManager pluginManager(PluginArgConfiguration pluginArgConfiguration) throws PluginException {
        IntegrationFactory integrationFactory = new DefaultIntegrationFactory();
        return integrationFactory.getPluginManager(pluginArgConfiguration);
    }

    @Bean
    public PluginApplication pluginApplication(){
        return new DefaultPluginApplication();
    }

    @Bean
    public PluginInitializer pluginInitializer(PluginApplication pluginApplication){
        return new AutoPluginInitializer(pluginApplication);
    }

}
