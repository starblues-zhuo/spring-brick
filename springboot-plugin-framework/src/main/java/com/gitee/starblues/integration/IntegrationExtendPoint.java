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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;

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
    public RealizeProvider realizeProvider(MainResourcePatternDefiner mainResourcePatternDefiner) {
        DefaultRealizeProvider defaultRealizeProvider = new DefaultRealizeProvider(configuration.environment());
        if(mainResourcePatternDefiner != null){
            defaultRealizeProvider.setMainResourcePatternDefiner(mainResourcePatternDefiner);
        }
        defaultRealizeProvider.init();
        return defaultRealizeProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public MainResourcePatternDefiner mainResourcePatternDefiner(){
        return new DefaultMainResourcePatternDefiner(configuration.mainPackage());
    }

    @Bean
    public ExtractFactory extractFactory(){
        return ExtractFactory.getInstant();
    }

}
