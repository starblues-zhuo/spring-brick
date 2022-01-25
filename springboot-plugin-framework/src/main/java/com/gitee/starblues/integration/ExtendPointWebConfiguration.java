package com.gitee.starblues.integration;

import com.gitee.starblues.integration.listener.SwaggerListener;
import com.gitee.starblues.spring.web.PluginStaticResourceConfig;
import com.gitee.starblues.spring.web.PluginStaticResourceWebMvcConfigurer;
import com.gitee.starblues.spring.web.thymeleaf.PluginThymeleafInvolved;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

/**
 * 系统web环境配置点
 * @author starBlues
 * @version 3.0.0
 */
@ConditionalOnWebApplication
public class ExtendPointWebConfiguration {

    private final GenericApplicationContext applicationContext;

    public ExtendPointWebConfiguration(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnClass(ResourceResolver.class)
    @ConditionalOnMissingBean
    public PluginStaticResourceWebMvcConfigurer pluginWebResourceResolver(PluginStaticResourceConfig resourceConfig){
        return new PluginStaticResourceWebMvcConfigurer(resourceConfig);
    }

    @Bean
    @ConditionalOnClass(ResourceResolver.class)
    @ConditionalOnMissingBean
    public PluginStaticResourceConfig pluginStaticResourceConfig(){
        return new PluginStaticResourceConfig();
    }

    @Bean
    @ConditionalOnClass({ TemplateMode.class, SpringTemplateEngine.class })
    @ConditionalOnProperty(name = "spring.thymeleaf.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean
    public PluginThymeleafInvolved pluginThymeleafInvolved(){
        return new PluginThymeleafInvolved();
    }

    @Bean
    @ConditionalOnClass({ DocumentationPluginsBootstrapper.class })
    @ConditionalOnMissingBean
    public SwaggerListener swaggerListener(){
        return new SwaggerListener(applicationContext);
    }

}
