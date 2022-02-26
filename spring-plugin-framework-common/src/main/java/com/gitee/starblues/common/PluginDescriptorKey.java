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

    public static final String MAIN_CLASS = "Main-Class";

    /** Must configure prop **/
    public static final String PLUGIN_ID = "Plugin-Id";
    public static final String PLUGIN_BOOTSTRAP_CLASS = "Plugin-Bootstrap-Class";
    public static final String PLUGIN_VERSION = "Plugin-Version";

    /** Optional configure prop **/
    public static final String PLUGIN_DESCRIPTION = "Plugin-Description";
    public static final String PLUGIN_PROVIDER = "Plugin-Provider";
    public static final String PLUGIN_DEPENDENCIES = "Plugin-Dependencies";
    public static final String PLUGIN_REQUIRES = "Plugin-Requires";
    public static final String PLUGIN_LICENSE = "Plugin-License";
    public static final String PLUGIN_CONFIG_FILE_NAME = "Plugin-Config-Filename";
    public static final String PLUGIN_CONFIG_FILE_LOCATION = "Plugin-Config-File-Location";

    /** System create prop **/
    public static final String PLUGIN_PATH = "Plugin-Path";
    public static final String PLUGIN_RESOURCES_CONFIG = "Plugin-Resources-Config";
    public static final String PLUGIN_PACKAGE_TYPE = "Plugin-Package-Type";


}
