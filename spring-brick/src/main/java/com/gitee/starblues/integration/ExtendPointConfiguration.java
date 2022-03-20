/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.integration;

import com.gitee.starblues.core.DefaultRealizeProvider;
import com.gitee.starblues.core.RealizeProvider;;
import com.gitee.starblues.core.launcher.plugin.BasicMainResourcePatternDefiner;
import com.gitee.starblues.integration.operator.DefaultPluginOperator;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.PluginOperatorWrapper;
import com.gitee.starblues.integration.user.DefaultPluginUser;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.spring.extract.ExtractFactory;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;

/**
 * 系统Bean配置
 * @author starBlues
 * @version 3.0.0
 */
public class ExtendPointConfiguration {

    private final GenericApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;

    public ExtendPointConfiguration(GenericApplicationContext applicationContext,
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
        DefaultRealizeProvider defaultRealizeProvider = new DefaultRealizeProvider(configuration, applicationContext);
        defaultRealizeProvider.init();
        return defaultRealizeProvider;
    }

    @Bean
    public ExtractFactory extractFactory(){
        return ExtractFactory.getInstant();
    }

    @Bean
    @ConditionalOnMissingBean
    public BasicMainResourcePatternDefiner mainResourcePatternDefiner(){
        return new BasicMainResourcePatternDefiner(configuration.mainPackage());
    }

}
