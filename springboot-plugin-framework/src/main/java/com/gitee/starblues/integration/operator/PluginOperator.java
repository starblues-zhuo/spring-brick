package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.PluginException;
import com.gitee.starblues.core.PluginWrapper;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * 操作插件的接口
 * @author starBlues
 * @version 2.4.4
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
     * @param jarPath 插件包的路径
     * @return  成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 校验异常
     */
    boolean verify(Path jarPath) throws PluginException;

    /**
     * 通过路径安装插件(会启用), 该插件文件必须存在于服务器 [适用于生产环境]
     * 如果在插件目录存在同名的插件包, 系统会自动备份该插件包。备份文件命名规则为；[install-backup][时间]_原jar名.jar
     * @param jarPath 插件路径
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo install(Path jarPath) throws PluginException;

    /**
     * 卸载插件 [适用于生产环境]
     * @param pluginId 插件id
     * @param isBackup 是否备份原来的插件。备份文件命名规则为；[uninstall][时间]_原jar名.jar
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean uninstall(String pluginId, boolean isBackup) throws PluginException;

    /**
     * 加载插件, 但不启动 [适用于生产环境]
     * @param jarPath 插件路径
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo load(Path jarPath) throws PluginException;

    /**
     * 加载插件, 但不启动 [适用于生产环境]
     * @param pluginFile 插件文件
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo load(MultipartFile pluginFile) throws PluginException;

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
     * 上传插件并启用插件。[适用于生产环境]
     * 如果在插件目录存在同名的插件包, 系统会自动备份该插件包。备份文件命名规则为；[install-backup][时间]_原jar名.jar
     * @param pluginFile 插件文件
     * @return 成功: 返回插件信息PluginInfo; 失败: 抛出异常或者返回null
     * @throws PluginException 异常信息
     */
    PluginInfo uploadPluginAndStart(MultipartFile pluginFile) throws PluginException;

    /**
     * 通过路径安装插件的配置文件。该文件必须存在于服务器。[适用于生产环境]
     * 如果配置文件目录存在同名的配置文件, 系统会自动备份该配置文件。备份文件命名规则为；[install-config-backup][时间]_原jar名.jar
     * @param configFilePath 配置文件路径。
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 安装异常
     */
    boolean installConfigFile(Path configFilePath) throws PluginException;
    
    
    /**
     * 上传配置文件。[适用于生产环境]
     * 如果配置文件目录存在同名的配置文件, 系统会自动备份该配置文件。备份文件命名规则为；[upload-config-backup][时间]_原jar名.jar
     * @param configFile 配置文件
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean uploadConfigFile(MultipartFile configFile) throws PluginException;

    /**
     * 通过路径备份文件。可备份插件和插件的配置文件。[适用于生产环境]
     * @param backDirPath 备份的目录路径
     * @param sign 备份文件的自定义标识
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean backupPlugin(Path backDirPath, String sign) throws PluginException;

    /**
     * 通过插件id备份插件。[适用于生产环境]
     * @param pluginId 插件id
     * @param sign 备份文件的自定义标识
     * @return 成功: 返回true; 失败: 抛出异常或者返回false
     * @throws PluginException 异常信息
     */
    boolean backupPlugin(String pluginId, String sign) throws PluginException;

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

    /**
     * 得到插件文件的路径
     * @return 返回插件路径列表
     * @throws PluginException 异常信息
     */
    Set<String> getPluginFilePaths();

    /**
     * 得到所有插件的包装类 [适用于生产环境、开发环境]
     * @return 返回插件包装类集合
     */
    List<PluginWrapper> getPluginWrapper();

    /**
     * 通过插件id得到插件的包装类 [适用于生产环境、开发环境]
     * @param pluginId 插件id
     * @return 返回插件包装类集合
     */
    PluginWrapper getPluginWrapper(String pluginId);


}
