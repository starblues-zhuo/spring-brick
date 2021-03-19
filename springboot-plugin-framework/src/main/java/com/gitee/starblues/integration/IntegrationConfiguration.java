package com.gitee.starblues.integration;

import org.pf4j.RuntimeMode;

import java.util.List;
import java.util.Set;


/**
 * 插件集成时的配置接口。插件集成的配置接口
 * @author starBlues
 * @version 2.4.2
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
     * 插件 RestController 统一请求的路径前缀
     * @return path
     */
    String pluginRestPathPrefix();

    /**
     * 是否启用插件id作为RestController的路径前缀。
     * 如果启用。则路径前缀为 pluginRestPathPrefix() 返回的路径拼接插件id,
     * 即为: /pathPrefix/pluginId/**
     * @return boolean
     */
    boolean enablePluginIdRestPathPrefix();


    /**
     * 是否启用该插件框架
     * @return true 启用, false 禁用
     */
    boolean enable();

    /**
     * 启用的插件id
     * @return Set
     */
    Set<String> enablePluginIds();

    /**
     * 禁用的插件id, 禁用后系统不会启动该插件
     * 如果禁用所有插件, 则Set集合中返回一个字符: *
     * @return Set
     */
    Set<String> disablePluginIds();

    /**
     * 是否启用Swagger刷新机制
     * @return 启用返回true, 不启用返回 false
     */
    boolean enableSwaggerRefresh();

    /**
     * 设置初始化时插件启动的顺序.
     * @return 有顺序的插件id
     */
    List<String> sortInitPluginIds();

    /**
     * 当前主程序的版本号, 用于校验插件是否可安装.
     * 插件中可通过插件配置信息 requires 来指定可安装的主程序版本
     * @return 系统版本号, 如果为: 0.0.0 的话, 表示不校验
     */
    String version();

    /**
     * 设置为true表示插件设置的requires的版本号完全匹配version版本号才可允许插件安装, 即: [requires]=[x.y.z]
     * 设置为false表示插件设置的requires的版本号小于等于version值, 插件就可安装, 即[requires]小于等于[x.y.z]
     * 默认为false
     * @return true or false
     */
    boolean exactVersionAllowed();

    /**
     * 是否启用webSocket功能. 如需启用, 则需要引入springboot支持的WebSocket依赖
     * @return 启用返回true,不启用返回false
     */
    boolean enableWebSocket();

}
