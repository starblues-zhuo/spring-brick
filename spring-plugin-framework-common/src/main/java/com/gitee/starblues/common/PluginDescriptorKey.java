package com.gitee.starblues.common;

/**
 * 插件描述文件配置key
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

    /** System create prop **/
    public static final String PLUGIN_PATH = "Plugin-Path";
    public static final String PLUGIN_RESOURCES_CONFIG = "Plugin-Resources-Config";


}
