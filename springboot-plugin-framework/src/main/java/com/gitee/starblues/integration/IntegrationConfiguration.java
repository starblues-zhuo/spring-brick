package com.gitee.starblues.integration;

import org.pf4j.RuntimeMode;
import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;


/**
 * 插件集成时的配置接口。插件集成的配置接口
 * @author zhangzhuo
 * @version 1.0
 */
public interface IntegrationConfiguration {

    /**
     * 运行环境。运行项目时的模式。分为开发环境(DEVELOPMENT)、生产环境(DEPLOYMENT)
     * @return RuntimeMode.DEVELOPMENT、RuntimeMode.DEPLOYMENT
     */
    RuntimeMode environment();

    /**
     * 插件的路径。开发环境建议直接配置为插件模块的父级目录。例如: plugins。如果启动主程序时, 插件为加载, 请检查该配置是否正确。
     * @return 插件的路径
     */
    String pluginPath();

    /**
     * 插件文件的配置路径。在生产环境下, 插件的配置文件路径。
     *  在生产环境下， 请将所有插件使用到的配置文件统一放到该路径下管理。
     *  在开发环境下，配置为空串。程序会自动从 resources 获取配置文件， 所以请确保编译后的target 下存在该配置文件
     * @return 插件文件的配置路径
     */
    String pluginConfigFilePath();

    /**
     * 上传插件包存储的临时路径。默认 temp(相对于主程序jar路径)。
     * @return 上传插件的临时保存路径。
     */
    String uploadTempPath();

    /**
     * 插件备份路径。默认 backupPlugin  (相对于主程序jar路径)。
     * @return 插件备份路径。
     */
    String backupPath();

    /**
     * 插件 RestController 统一请求的路径前缀。只有 pluginRestControllerPathPrefix
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
