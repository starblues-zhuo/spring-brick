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

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;

/**
 * 插件PluginStaticResourceConfig
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class PluginStaticResourceConfig {

    public static final String DEFAULT_PLUGIN_STATIC_RESOURCE_PATH_PREFIX = "static-plugin";
    private static final Logger log = LoggerFactory.getLogger(PluginStaticResourceConfig.class);

    private String pathPrefix = DEFAULT_PLUGIN_STATIC_RESOURCE_PATH_PREFIX;
    private CacheControl cacheControl = CacheControl.noCache();

    public void logPathPrefix(){
        log.info("插件静态资源访问前缀配置为: /{}/{pluginId}", pathPrefix);
    }
}
