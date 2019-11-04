package com.gitee.starblues.integration.operator;

import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import org.pf4j.PluginWrapper;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * 操作插件的接口
 * @author zhangzhuo
 * @version 2.2.0
 * @see DefaultPluginOperator
 */
public interface PluginOperator {

    /**
     * 初始化插件。该方法只能执行一次。
     * @param pluginInitializerListener 插件初始化监听者
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws Exception 异常信息
     */
    boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws Exception;
    

    /**
     * 通过路径安装插件(会启用), 该插件文件必须存在于服务器 [适用于生产环境]
     * 如果在插件目录存在同名的插件包, 系统会自动备份该插件包。备份文件命名规则为；[install-backup][时间]_原jar名.jar
     * @param path 插件路径
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws Exception 异常信息
     */
    boolean install(Path path) throws Exception;


    /**
     * 卸载插件 [适用于生产环境]
     * @param pluginId 插件id
     * @param isBackup 是否备份原来的插件。备份文件命名规则为；[uninstall][时间]_原jar名.jar
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws Exception 异常信息
     */
    boolean uninstall(String pluginId, boolean isBackup) throws Exception;
    
    /**
     * 启用插件 [适用于生产环境、开发环境]
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws Exception 异常信息
     */
    boolean start(String pluginId) throws Exception;


    /**
     * 停止插件 [适用于生产环境、开发环境]
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws Exception 异常信息
     */
    boolean stop(String pluginId) throws Exception;
    

    /**
     * 上传插件并启用插件。[适用于生产环境]
     * 如果在插件目录存在同名的插件包, 系统会自动备份该插件包。备份文件命名规则为；[install-backup][时间]_原jar名.jar
     * @param pluginFile 配置文件
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws Exception 异常信息
     */
    boolean uploadPluginAndStart(MultipartFile pluginFile) throws Exception;

    /**
     * 通过路径安装插件的配置文件。该文件必须存在于服务器。[适用于生产环境]
     * 如果配置文件目录存在同名的配置文件, 系统会自动备份该配置文件。备份文件命名规则为；[install-config-backup][时间]_原jar名.jar
     * @param path 配置文件路径。
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws Exception 安装异常
     */
    boolean installConfigFile(Path path) throws Exception;
    
    
    /**
     * 上传配置文件。[适用于生产环境]
     * 如果配置文件目录存在同名的配置文件, 系统会自动备份该配置文件。备份文件命名规则为；[upload-config-backup][时间]_原jar名.jar
     * @param configFile 配置文件
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws Exception 异常信息
     */
    boolean uploadConfigFile(MultipartFile configFile) throws Exception;

    /**
     * 通过路径备份文件。可备份插件和插件的配置文件。[适用于生产环境]
     * @param path 路径
     * @param sign 备份文件的自定义标识
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws Exception 异常信息
     */
    boolean backupPlugin(Path path, String sign) throws Exception;

    /**
     * 通过插件id备份插件。[适用于生产环境]
     * @param pluginId 插件id
     * @param sign 备份文件的自定义标识
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws Exception 异常信息
     */
    boolean backupPlugin(String pluginId, String sign) throws Exception;

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
     * 得到插件文件的路径 [适用于生产环境]
     * @return 返回插件路径列表
     * @throws Exception 异常信息
     */
    Set<String> getPluginFilePaths() throws Exception;

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
