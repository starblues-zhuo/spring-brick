package com.plugin.development.integration.operator;

import com.plugin.development.exception.PluginPlugException;
import com.plugin.development.integration.initialize.PluginInitializerListener;
import com.plugin.development.integration.operator.module.PluginInfo;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * 操作插件的接口
 * @author zhangzhuo
 * @version 1.0
 * @see com.plugin.development.integration.operator.DefaultPluginOperator
 */
public interface PluginOperator {

    /**
     * 初始化插件
     * @param pluginInitializerListener 插件初始化监听者
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginPlugException;

    /**
     * 通过路径加载插件(不会启用)
     * @param path 插件路径
     * @return 成功返回插件id
     * @throws PluginPlugException 插件插头异常
     */
    String loadPlugin(Path path) throws PluginPlugException;

    /**
     * 通过路径安装插件(会启用)
     * @param path 插件路径
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean install(Path path) throws PluginPlugException;


    /**
     * 卸载插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean uninstall(String pluginId) throws PluginPlugException;

    /**
     * 通过插件id删除插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean delete(String pluginId) throws PluginPlugException;


    /**
     * 通过路径删除插件
     * @param path 插件路径
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean delete(Path path) throws PluginPlugException;

    /**
     * 启用插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean start(String pluginId) throws PluginPlugException;


    /**
     * 停止插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean stop(String pluginId) throws PluginPlugException;


    /**
     * 上传插件
     * @param pluginFile 插件文件
     * @return 成功返回插件路径.不成功返回null, 或者抛出异常
     * @throws PluginPlugException 插件插头异常
     */
    Path uploadPlugin(MultipartFile pluginFile) throws PluginPlugException;


    /**
     * 上传插件并启用插件
     * @param pluginFile 配置文件
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws PluginPlugException 插件插头异常
     */
    boolean uploadPluginAndStart(MultipartFile pluginFile) throws PluginPlugException;

    /**
     * 上传配置文件(如果存在, 则覆盖)
     * @param configFile 配置文件
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws PluginPlugException 插件插头异常
     */
    boolean uploadConfigFile(MultipartFile configFile) throws PluginPlugException;

    /**
     * 通过路径备份插件文件
     * @param path 路径
     * @param appendName 追加的名称
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws PluginPlugException 插件插头异常
     */
    boolean backupPlugin(Path path, String appendName) throws PluginPlugException;

    /**
     * 通过插件id备份插件
     * @param pluginId 插件id
     * @param appendName 追加的名称
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws PluginPlugException 插件插头异常
     */
    boolean backupPlugin(String pluginId, String appendName) throws PluginPlugException;

    /**
     * 获取插件信息
     * @return 返回插件信息列表
     */
    List<PluginInfo> getPluginInfo();


    /**
     * 得到插件文件的路径
     * @return 返回插件路径列表
     * @throws PluginPlugException 插件插头异常
     */
    Set<String> getPluginFilePaths() throws PluginPlugException;


}
