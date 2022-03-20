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

import com.gitee.starblues.integration.listener.SwaggerListener;
import com.gitee.starblues.spring.web.PluginStaticResourceConfig;
import com.gitee.starblues.spring.web.PluginStaticResourceWebMvcConfigurer;
import com.gitee.starblues.spring.web.thymeleaf.PluginThymeleafInvolved;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
@Import({
        ExtendPointWebConfiguration.PluginStaticResourceConfiguration.class,
        ExtendPointWebConfiguration.PluginThymeleafConfiguration.class,
        ExtendPointWebConfiguration.SwaggerListenerConfiguration.class,
})
public class ExtendPointWebConfiguration {


    @ConditionalOnClass(ResourceResolver.class)
    public static class PluginStaticResourceConfiguration{

        @Bean
        @ConditionalOnMissingBean
        public PluginStaticResourceWebMvcConfigurer pluginWebResourceResolver(PluginStaticResourceConfig resourceConfig){
            return new PluginStaticResourceWebMvcConfigurer(resourceConfig);
        }

        @Bean
        @ConditionalOnMissingBean
        public PluginStaticResourceConfig pluginStaticResourceConfig() {
            return new PluginStaticResourceConfig();
        }
    }

    @ConditionalOnClass({ TemplateMode.class, SpringTemplateEngine.class })
    @ConditionalOnProperty(name = "spring.thymeleaf.enabled", havingValue = "true", matchIfMissing = true)
    public static class PluginThymeleafConfiguration{

        @Bean
        @ConditionalOnMissingBean
        public PluginThymeleafInvolved pluginThymeleafInvolved(){
            return new PluginThymeleafInvolved();
        }
    }

    @ConditionalOnClass({ DocumentationPluginsBootstrapper.class })
    public static class SwaggerListenerConfiguration {

        private final GenericApplicationContext applicationContext;

        public SwaggerListenerConfiguration(GenericApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Bean
        @ConditionalOnMissingBean
        public SwaggerListener swaggerListener(){
            return new SwaggerListener(applicationContext);
        }

    }

}
