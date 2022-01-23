package com.gitee.starblues.integration;

import com.gitee.starblues.core.DefaultRealizeProvider;
import com.gitee.starblues.core.RealizeProvider;
import com.gitee.starblues.core.classloader.DefaultMainResourcePatternDefiner;
import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.PluginOperatorWrapper;
import com.gitee.starblues.integration.user.DefaultPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.spring.extract.ExtractFactory;
import com.gitee.starblues.spring.web.PluginStaticResourceConfig;
import com.gitee.starblues.spring.web.PluginStaticResourceResolver;
import com.gitee.starblues.spring.web.PluginStaticResourceWebMvcConfigurer;
import com.gitee.starblues.spring.web.thymeleaf.PluginThymeleafInvolved;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * 集成扩展点
 * @author starBlues
 * @version 3.0.0
 */
public class IntegrationExtendPoint {

    private final GenericApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;

    public IntegrationExtendPoint(GenericApplicationContext applicationContext,
                                  IntegrationConfiguration configuration) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginUser createPluginUser() {
        return new DefaultPluginUser(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public PluginOperator createPluginOperator(RealizeProvider realizeProvider) {
        PluginOperator pluginOperator = new DefaultPluginOperator(
                applicationContext,
                realizeProvider,
                configuration
        );
        return new PluginOperatorWrapper(pluginOperator, configuration);
    }

    @Bean
    @ConditionalOnMissingBean
    public RealizeProvider realizeProvider() {
        DefaultRealizeProvider defaultRealizeProvider = new DefaultRealizeProvider(configuration.environment());
        defaultRealizeProvider.init();
        return defaultRealizeProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public MainResourcePatternDefiner mainResourcePatternDefiner(){
        return new DefaultMainResourcePatternDefiner(configuration.mainPackage());
    }

    @Bean
    @ConditionalOnClass(ResourceResolver.class)
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean
    public PluginStaticResourceWebMvcConfigurer pluginWebResourceResolver(PluginStaticResourceConfig resourceConfig){
        return new PluginStaticResourceWebMvcConfigurer(resourceConfig);
    }

    @Bean
    @ConditionalOnClass(ResourceResolver.class)
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean
    public PluginStaticResourceConfig pluginStaticResourceConfig(){
        return new PluginStaticResourceConfig();
    }

    @Bean
    @ConditionalOnClass({ TemplateMode.class, SpringTemplateEngine.class })
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "spring.thymeleaf.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public PluginThymeleafInvolved pluginThymeleafInvolved(){
        return new PluginThymeleafInvolved();
    }

    @Bean
    public ExtractFactory extractFactory(){
        return ExtractFactory.getInstant();
    }

}
