package com.gitee.starblues.core.descriptor;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * 插件信息
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginDescriptor {

    /**
     * 获取插件id
     * @return String
     */
    String getPluginId();

    /**
     * 获取插件版本
     * @return String
     */
    String getPluginVersion();

    /**
     * 获取插件引导类
     * @return String
     */
    String getPluginBootstrapClass();

    /**
     * 获取插件路径
     * @return Path
     */
    Path getPluginPath();

    /**
     * 获取插件classes path路径
     * @return Path
     */
    String getPluginClassPath();

    /**
     * 获取插件依赖的路径
     * @return String
     */
    Set<String> getPluginLibPaths();

    /**
     * 获取插件描述
     * @return String
     */
    String getDescription();

    /**
     * 获取插件所需主程序版本
     * @return String
     */
    String getRequires();

    /**
     * 获取插件提供开发者
     * @return String
     */
    String getProvider();

    /**
     * 获取插件 license
     * @return String
     */
    String getLicense();

    /**
     * 获取插件配置文件名称
     * @return String
     */
    String getConfigFileName();

    /**
     * 获取当前插件依赖
     * @return List
     */
    List<PluginDependency> getPluginDependency();

    /**
     * 得到插件的 Manifest 文件
     * @return Manifest
     */
    Manifest getManifest();

    /**
     * 得到插件类型
     * @return 插件类型
     */
    Type getType();

    /**
     * 插件类型
     */
    enum Type{
        /**
         * jar文件
         */
        JAR,

        /**
         * zip 文件
         */
        ZIP,

        /**
         * 生产模式目录
         */
        DIR_OF_PROD,

        /**
         * 开发模式目录
         */
        DIR_OF_DEV
    }


}
