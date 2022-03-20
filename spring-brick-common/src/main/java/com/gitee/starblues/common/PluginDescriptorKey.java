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

package com.gitee.starblues.common;

/**
 * 插件描述文件配置key
 *
 * @author starBlues
 * @version 3.0.0
 */
public class PluginDescriptorKey {


    /** Must configure prop **/
    public static final String PLUGIN_ID = "plugin.id";
    public static final String PLUGIN_BOOTSTRAP_CLASS = "plugin.bootstrapClass";
    public static final String PLUGIN_VERSION = "plugin.version";

    /** Optional configure prop **/
    public static final String PLUGIN_DESCRIPTION = "plugin.description";
    public static final String PLUGIN_PROVIDER = "plugin.provider";
    public static final String PLUGIN_DEPENDENCIES = "plugin.dependencies";
    public static final String PLUGIN_REQUIRES = "plugin.requires";
    public static final String PLUGIN_LICENSE = "plugin.license";
    public static final String PLUGIN_CONFIG_FILE_NAME = "plugin.configFileName";
    public static final String PLUGIN_CONFIG_FILE_LOCATION = "plugin.configFileLocation";
    public static final String PLUGIN_ARGS = "plugin.args";

    /** System create prop **/
    public static final String PLUGIN_PATH = "plugin.system.path";
    public static final String PLUGIN_RESOURCES_CONFIG = "plugin.system.resourcesConfig";


}
