package com.gitee.starblues.integration;

import com.gitee.starblues.integration.application.AutoPluginApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author starBlues
 * @version 3.0.0
 */
@Configuration(proxyBeanMethods = true)
@EnableConfigurationProperties(AutoIntegrationConfiguration.class)
@ConditionalOnExpression("${" + AutoIntegrationConfiguration.ENABLE_STARTER_KEY + ":true}")
@Import(AutoPluginApplication.class)
public class SpringBootPluginStarter {

}
