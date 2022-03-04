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

package com.gitee.starblues.spring.web;

import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 插件静态资源webMvc配置
 * @author starBlues
 * @version 3.0.0
 */
public class PluginStaticResourceWebMvcConfigurer implements WebMvcConfigurer {

    private final PluginStaticResourceConfig resourceConfig;

    public PluginStaticResourceWebMvcConfigurer(PluginStaticResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathPattern = "/" + resourceConfig.getPathPrefix() + "/**";
        ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(pathPattern);
        CacheControl cacheControl = resourceConfig.getCacheControl();
        if(cacheControl != null){
            resourceHandlerRegistration.setCacheControl(cacheControl);
        } else {
            resourceHandlerRegistration.setCacheControl(CacheControl.noStore());
        }
        resourceHandlerRegistration
                .resourceChain(false)
                .addResolver(new PluginStaticResourceResolver());
    }


}
