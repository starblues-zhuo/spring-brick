/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.plugin.pack;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

/**
 * 插件信息
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class PluginInfo {

    /**
     * 插件id
     */
    @Parameter(required = true)
    private String id;

    /**
     * 插件引导启动类
     */
    @Parameter(required = true)
    private String bootstrapClass;

    /**
     * 插件版本
     */
    @Parameter(required = true)
    private String version;

    /**
     * 插件配置文件名称。
     */
    private String configFileName;

    /**
     * 插件配置文件所在目录。如果不填写, 默认从 target/classes 下读取
     */
    private String configFileLocation;

    /**
     * 插件描述
     */
    private String description;

    /**
     * 插件提供者
     */
    private String provider;

    /**
     * 需要安装的主程序版本
     */
    private String requires;

    /**
     * 插件 license
     */
    private String license;

    /**
     * 依赖的插件
     */
    private List<DependencyPlugin> dependencyPlugins;

}
