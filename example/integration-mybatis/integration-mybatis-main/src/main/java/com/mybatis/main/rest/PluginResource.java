package com.mybatis.main.rest;

import com.gitee.starblues.integration.PluginApplication;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * 插件jar 包测试功能
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("/plugin")
public class PluginResource {


    private final PluginOperator pluginOperator;

    @Autowired
    public PluginResource(PluginApplication pluginApplication) {
        this.pluginOperator = pluginApplication.getPluginOperator();
    }

    /**
     * 获取插件信息
     * @return 返回插件信息
     */
    @GetMapping
    public List<PluginInfo> getPluginInfo(){
        return pluginOperator.getPluginInfo();
    }

    /**
     * 获取插件jar文件名
     * @return 获取插件文件名。只在生产环境显示
     */
    @GetMapping("/files")
    public Set<String> getPluginFilePaths(){
        try {
            return pluginOperator.getPluginFilePaths();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据插件id停止插件
     * @param id 插件id
     * @return 返回操作结果
     */
    @PostMapping("/stop/{id}")
    public String stop(@PathVariable("id") String id){
        try {
            pluginOperator.stop(id);
            return "plugin<" + id +"> stop success";
        } catch (Exception e) {
            e.printStackTrace();
            return "plugin<" + id +"> stop failure : " + e.getMessage();
        }
    }

    /**
     * 根据插件id启动插件
     * @param id 插件id
     * @return 返回操作结果
     */
    @PostMapping("/start/{id}")
    public String start(@PathVariable("id") String id){
        try {
            pluginOperator.start(id);
            return "plugin<" + id +"> start success";
        } catch (Exception e) {
            e.printStackTrace();
            return "plugin<" + id +"> start failure : " + e.getMessage();
        }
    }


    /**
     * 根据插件id卸载插件
     * @param id 插件id
     * @return 返回操作结果
     */
    @PostMapping("/uninstall/{id}")
    public String uninstall(@PathVariable("id") String id){
        try {
            pluginOperator.uninstall(id);
            return "plugin<" + id +"> uninstall success";
        } catch (Exception e) {
            e.printStackTrace();
            return "plugin<" + id +"> uninstall failure : " + e.getMessage();
        }
    }


    /**
     * 根据插件路径安装插件。该插件jar必须在服务器上存在。注意: 该操作只适用于生产环境
     * @param path 插件路径名称
     * @return 操作结果
     */
    @PostMapping("/installByPath")
    public String install(@RequestParam("path") String path){
        try {
            pluginOperator.install(Paths.get(path));
            return "installByPath success";
        } catch (Exception e) {
            e.printStackTrace();
            return "installByPath failure : " + e.getMessage();
        }
    }


    /**
     * 上传并安装插件。注意: 该操作只适用于生产环境
     * @param multipartFile 上传文件 multipartFile
     * @return 操作结果
     */
    @PostMapping("/uploadInstallPluginJar")
    public String install(@RequestParam("jarFile") MultipartFile multipartFile){
        try {
            pluginOperator.uploadPluginAndStart(multipartFile);
            return "install success";
        } catch (Exception e) {
            e.printStackTrace();
            return "install failure : " + e.getMessage();
        }
    }


    /**
     * 上传插件的配置文件。注意: 该操作只适用于生产环境
     * @param multipartFile 上传文件 multipartFile
     * @return 操作结果
     */
    @PostMapping("/uploadPluginConfigFile")
    public String uploadConfig(@RequestParam("configFile") MultipartFile multipartFile){
        try {
            pluginOperator.uploadConfigFile(multipartFile);
            return "uploadConfig success";
        } catch (Exception e) {
            e.printStackTrace();
            return "uploadConfig failure : " + e.getMessage();
        }
    }


    /**
     * 通过插件id删除插件。注意: 该操作只适用于生产环境
     * @param pluginId 插件id
     * @return 操作结果
     */
    @DeleteMapping("/pluginId/{pluginId}")
    public String deleteById(@PathVariable("pluginId") String pluginId){
        try {
            pluginOperator.delete(pluginId);
            return "deleteById success";
        } catch (Exception e) {
            e.printStackTrace();
            return "deleteById failure : " + e.getMessage();
        }
    }

    /**
     * 通过路径删除插件。注意: 该操作只适用于生产环境
     * @param pluginJarPath 插件jar路径
     * @return 操作结果
     */
    @PostMapping("/path")
    public String deleteByPath(@RequestParam("pluginJarPath") String pluginJarPath){
        try {
            pluginOperator.delete(Paths.get(pluginJarPath));
            return "deleteByPath success";
        } catch (Exception e) {
            e.printStackTrace();
            return "deleteByPath failure : " + e.getMessage();
        }
    }

    /**
     * 备份插件。注意: 该操作只适用于生产环境
     * @param pluginId 插件id
     * @return 操作结果
     */
    @PostMapping("/back/{pluginId}")
    public String backupPlugin(@PathVariable("pluginId") String pluginId){
        try {
            pluginOperator.backupPlugin(pluginId, "testBack");
            return "backupPlugin success";
        } catch (Exception e) {
            e.printStackTrace();
            return "backupPlugin failure : " + e.getMessage();
        }
    }

}
