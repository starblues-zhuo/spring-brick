package com.gitee.starblues.integration;

import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.integration.application.AutoPluginApplication;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.realize.PluginUtils;
import com.gitee.starblues.spring.SpringPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 3.0.0
 */
@Configuration(proxyBeanMethods = true)
@EnableConfigurationProperties(AutoIntegrationConfiguration.class)
@ConditionalOnProperty(value = AutoIntegrationConfiguration.ENABLE_KEY, havingValue = "true", matchIfMissing = false)
public class SpringBootPluginStarter extends AutoPluginApplication {


    @Bean
    @ConditionalOnMissingBean
    @Override
    protected PluginUser createPluginUser(ApplicationContext applicationContext) {
        return super.createPluginUser(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected PluginOperator createPluginOperator(ApplicationContext applicationContext, IntegrationConfiguration configuration) {
        return super.createPluginOperator(applicationContext, configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected SpringPlugin createSpringPlugin(GenericApplicationContext applicationContext) {
        return super.createSpringPlugin(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    protected RealizeProvider createRealizeProvider(IntegrationConfiguration configuration) {
        return super.createRealizeProvider(configuration);
    }

}
