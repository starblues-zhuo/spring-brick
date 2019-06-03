package com.plugin.development.integration;

import org.pf4j.RuntimeMode;


/**
 * @Description: 插件集成时的配置接口。插件集成的配置接口
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-26 19:14
 * @Update Date Time:
 * @see
 */
public interface IntegrationConfiguration {

    /**
     * 运行环境
     * @return
     */
    RuntimeMode environment();

    /**
     * 插件的路径
     * @return
     */
    String pluginPath();

    /**
     * 插件文件的配置路径
     * @return
     */
    String pluginConfigFilePath();

    /**
     * 上传插件的临时保存路径。默认: temp, 替换默认请重写
     * @return
     */
    default String uploadTempPath(){
        return "temp";
    }

    /**
     * 插件备份路径。默认: backupPlugin, 替换默认请重写
     * @return
     */
    default String backupPath(){
        return "backupPlugin";
    }

}
