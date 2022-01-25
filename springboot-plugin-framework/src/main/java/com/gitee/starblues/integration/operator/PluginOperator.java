package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.upload.UploadByInputStreamParam;
import com.gitee.starblues.integration.operator.upload.UploadByMultipartFileParam;
import com.gitee.starblues.integration.operator.upload.UploadParam;

import java.nio.file.Path;
import java.util.List;

/**
 * 操作插件的接口
 * @author starBlues
 * @version 3.0.0
 * @see DefaultPluginOperator
 */
public interface PluginOperator {

    /**
     * 初始化插件。该方法只能执行一次。
     * @param pluginInitializerListener 插件初始化监听者
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException;

    /**
     * 校验插件jar包
     * @param pluginPath 插件包的路径
     * @return  成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 校验异常
     */
    boolean verify(Path pluginPath) throws PluginException;

    /**
     * 解析插件包
     * @param pluginPath 插件包路基
     * @return 解析的插件信息
     * @throws PluginException 插件异常
     */
    PluginInfo parse(Path pluginPath) throws PluginException;

    /**
     * 通过路径安装插件(会启用), 该插件文件必须存在于服务器 [适用于生产环境]
     * 如果在插件目录存在同名的插件包, 系统会自动备份该插件包。备份文件命名规则为；[install-backup][时间]_原jar名.jar
     * @param pluginPath 插件路径
     * @param unpackPlugin 是否解压插件包. (如果插件包为压缩包时生效)
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo install(Path pluginPath, boolean unpackPlugin) throws PluginException;

    /**
     * 卸载插件 [适用于生产环境]
     * @param pluginId 插件id
     * @param isDelete 卸载后是否删除插件文件.
     * @param isBackup 删除插件文件前, 是否备份插件文件。备份文件命名规则为；[uninstall][时间]_原jar名.jar
     * @throws PluginException 异常信息
     */
    void uninstall(String pluginId, boolean isDelete, boolean isBackup) throws PluginException;

    /**
     * 加载插件, 但不启动 [适用于生产环境]
     * @param jarPath 插件路径
     * @param unpackPlugin 是否解压插件包. (如果插件包为压缩包时生效)
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo load(Path jarPath, boolean unpackPlugin) throws PluginException;

    /**
     * 配合load使用. 针对load的插件进行unload [适用于生产环境]
     * @param pluginId 插件id
     * @param isBackup 是否备份原来的插件。备份文件命名规则为；[uninstall][时间]_原jar名.jar
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean unload(String pluginId, boolean isBackup) throws PluginException;

    /**
     * 启用插件 [适用于生产环境、开发环境]
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean start(String pluginId) throws PluginException;


    /**
     * 停止插件 [适用于生产环境、开发环境]
     * @param pluginId 插件id
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean stop(String pluginId) throws PluginException;

    /**
     * 上传插件.
     *   如果不存在相同插件(插件id不相同), 则正常上传到插件目录, 然后加载、启动。
     *   如果存在相同插件(插件id相同)
     *      相同插件在启动状态, 则进入更新模式, 满足上传的插件包版本必须大于已启动的插件版本, 系统会自动卸载旧版本, 安装新版本。
     *      相同插件不在启动状态, 则进入覆盖模式, 会对旧插件进行备份(可根据isBackOldPlugin配置不备份), 然后上传新插件包到插件目录, 然后加载、启动。
     *   如果存在同文件名称插件, 系统会抛出异常, 建议重命名插件名称, 再上传。
     *
     * @param uploadParam 上传参数
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     * @see UploadByInputStreamParam
     * @see UploadByMultipartFileParam
     */
    PluginInfo uploadPlugin(UploadParam uploadParam) throws PluginException;

    /**
     * 通过路径备份文件。可备份插件和插件的配置文件。[适用于生产环境]
     * @param backDirPath 备份的目录路径
     * @param sign 备份文件的自定义标识
     * @return 备份插件的路径
     * @throws PluginException 异常信息
     */
    Path backupPlugin(Path backDirPath, String sign) throws PluginException;

    /**
     * 通过插件id备份插件。[适用于生产环境]
     * @param pluginId 插件id
     * @param sign 备份文件的自定义标识
     * @return 备份插件的路径
     * @throws PluginException 异常信息
     */
    Path backupPlugin(String pluginId, String sign) throws PluginException;

    /**
     * 获取插件信息 [适用于生产环境、开发环境]
     * @return 返回插件信息列表
     */
    List<PluginInfo> getPluginInfo();

    /**
     * 根据插件id获取插件信息 [适用于生产环境、开发环境]
     * @param pluginId 插件id
     * @return 插件信息
     */
    PluginInfo getPluginInfo(String pluginId);

}
