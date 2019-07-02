package com.plugin.example.start.rest;

import com.gitee.starblues.exception.PluginPlugException;
import com.gitee.starblues.integration.PluginApplication;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

/**
 * @Description: 测试操作插件功能
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-31 12:39
 * @Update Date Time:
 * @see
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
     * @return
     */
    @GetMapping
    public List<PluginInfo> getPluginInfo(){
        return pluginOperator.getPluginInfo();
    }

    /**
     * 获取插件文件名
     * @return
     */
    @GetMapping("/files")
    public Set<String> getPluginFilePaths(){
        try {
            return pluginOperator.getPluginFilePaths();
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 停止插件(
     * @param id
     * @return
     */
    @PostMapping("/stop/{id}")
    public String stop(@PathVariable("id") String id){
        try {
            pluginOperator.stop(id);
            return "plugin<" + id +"> stop success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "plugin<" + id +"> stop failure : " + e.getMessage();
        }
    }

    /**
     * 启动插件
     * @param id
     * @return
     */
    @PostMapping("/start/{id}")
    public String start(@PathVariable("id") String id){
        try {
            pluginOperator.start(id);
            return "plugin<" + id +"> start success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "plugin<" + id +"> start failure : " + e.getMessage();
        }
    }


    /**
     * 卸载插件
     * @param id
     * @return
     */
    @PostMapping("/uninstall/{id}")
    public String uninstall(@PathVariable("id") String id){
        try {
            pluginOperator.uninstall(id);
            return "plugin<" + id +"> uninstall success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "plugin<" + id +"> uninstall failure : " + e.getMessage();
        }
    }





    /**
     * 安装插件
     * @param path 插件路径名称
     * @return
     */
    @PostMapping("/installByPath/{path}")
    public String install(@PathVariable("path") String path){
        try {
            pluginOperator.install(Paths.get(path));
            return "installByPath success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "installByPath failure : " + e.getMessage();
        }
    }


    /**
     * 上传并安装插件
     * @param
     * @return
     */
    @PostMapping("/install")
    public String install(@RequestParam("file") MultipartFile multipartFile){
        try {
            pluginOperator.uploadPluginAndStart(multipartFile);
            return "install success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "install failure : " + e.getMessage();
        }
    }


    /**
     * 上传并安装插件
     * @param
     * @return
     */
    @PostMapping("/uploadConfig")
    public String uploadConfig(@RequestParam("file") MultipartFile multipartFile){
        try {
            pluginOperator.uploadConfigFile(multipartFile);
            return "uploadConfig success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "uploadConfig failure : " + e.getMessage();
        }
    }


    /**
     * 通过插件id删除插件
     * @param
     * @return
     */
    @DeleteMapping("/pluginId/{pluginId}")
    public String deleteById(@PathVariable("pluginId") String pluginId){
        try {
            pluginOperator.delete(pluginId);
            return "deleteById success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "deleteById failure : " + e.getMessage();
        }
    }

    /**
     * 通过路径删除插件
     * @param
     * @return
     */
    @PostMapping("/path")
    public String deleteByPath(@RequestParam("pluginPath") String path){
        try {
            pluginOperator.delete(Paths.get(path));
            return "deleteByPath success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "deleteByPath failure : " + e.getMessage();
        }
    }

    /**
     * 备份插件
     * @param
     * @return
     */
    @PostMapping("/back/{pluginId}")
    public String backupPlugin(@PathVariable("pluginId") String pluginId){
        try {
            pluginOperator.backupPlugin(pluginId, "testBack");
            return "backupPlugin success";
        } catch (PluginPlugException e) {
            e.printStackTrace();
            return "backupPlugin failure : " + e.getMessage();
        }
    }

}
