package com.plugin.development.integration;

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
     * 上传插件的临时保存路径。默认: temp, 替换默认请重写
     * @return 上传插件的临时保存路径。默认: temp, 替换默认请重写
     */
    default String uploadTempPath(){
        return "temp";
    }

    /**
     * 插件备份路径。默认: backupPlugin, 替换默认请重写
     * @return 插件备份路径。默认: backupPlugin, 替换默认请重写
     */
    default String backupPath(){
        return "backupPlugin";
    }

}
