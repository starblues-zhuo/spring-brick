package com.gitee.starblues.integration;

import org.pf4j.RuntimeMode;


/**
 * 插件集成时的配置接口。插件集成的配置接口
 * @author zhangzhuo
 * @version 1.0
 */
public interface IntegrationConfiguration {

    /**
     * 运行环境
     * @return 运行环境
     */
    RuntimeMode environment();

    /**
     * 插件的路径
     * @return 插件的路径
     */
    String pluginPath();

    /**
     * 插件文件的配置路径
     * @return 插件文件的配置路径
     */
    String pluginConfigFilePath();

    /**
     * 上传插件的临时保存路径。
     * @return 上传插件的临时保存路径。
     */
    String uploadTempPath();

    /**
     * 插件备份路径。
     * @return 插件备份路径。
     */
    String backupPath();

    /**
     * 统一插件RestController的路径前缀
     * @return path
     */
    String pluginRestControllerPathPrefix();

    /**
     * 启用插件id作为RestController的路径前缀。
     * 如果启用。则路径前缀为 pluginRestControllerPathPrefix() 返回的路径拼接插件id,
     * 即为: /pathPrefix/pluginId/**
     * @return boolean
     */
    boolean enablePluginIdRestControllerPathPrefix();

}
