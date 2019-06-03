package com.plugin.development.integration.operator;

import com.plugin.development.exception.PluginPlugException;
import com.plugin.development.integration.operator.module.PluginInfo;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @Description: 操作插件的接口
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 10:30
 * @Update Date Time:
 * @see
 */
public interface PluginOperator {

    /**
     * 初始化插件
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException 插件插头异常
     */
    boolean initPlugins() throws PluginPlugException;

    /**
     * 通过路径加载插件(不会启用)
     * @param path 插件路径
     * @return 成功返回插件id
     * @throws PluginPlugException
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
     * @throws PluginPlugException
     */
    boolean uninstall(String pluginId) throws PluginPlugException;

    /**
     * 通过插件id删除插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException
     */
    boolean delete(String pluginId) throws PluginPlugException;


    /**
     * 通过路径删除插件
     * @param path 插件路径
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException
     */
    boolean delete(Path path) throws PluginPlugException;

    /**
     * 启用插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException
     */
    boolean start(String pluginId) throws PluginPlugException;


    /**
     * 停止插件
     * @param pluginId 插件id
     * @return 成功返回true.不成功抛出异常或者返回false
     * @throws PluginPlugException
     */
    boolean stop(String pluginId) throws PluginPlugException;


    /**
     * 上传插件
     * @param pluginFile 插件文件
     * @return 成功返回插件路径.不成功返回null, 或者抛出异常
     * @throws PluginPlugException
     */
    Path uploadPlugin(MultipartFile pluginFile) throws PluginPlugException;


    /**
     * 上传插件并启用插件
     * @param pluginFile 配置文件
     * @return 成功返回true.不成功返回false, 或者抛出异常
     * @throws PluginPlugException
     */
    boolean uploadPluginAndStart(MultipartFile pluginFile) throws PluginPlugException;

    /**
     * 上传配置文件(如果存在, 则覆盖)
     * @param configFile
     * @return
     * @throws PluginPlugException
     */
    boolean uploadConfigFile(MultipartFile configFile) throws PluginPlugException;

    /**
     * 通过路径备份插件文件
     * @param path 路径
     * @param appendName 追加的名称
     * @return
     * @throws PluginPlugException
     */
    boolean backupPlugin(Path path, String appendName) throws PluginPlugException;

    /**
     * 通过插件id备份插件
     * @param pluginId 插件id
     * @param appendName 追加的名称
     * @return
     * @throws PluginPlugException
     */
    boolean backupPlugin(String pluginId, String appendName) throws PluginPlugException;

    /**
     * 获取插件信息
     * @return
     */
    List<PluginInfo> getPluginInfo();


    /**
     * 得到插件文件的路径
     * @return
     * @throws PluginPlugException
     */
    Set<String> getPluginFilePaths() throws PluginPlugException;


}
