package com.gitee.starblues.core.scanner;

import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的插件扫描者
 * @author starBlues
 * @version 3.0.0
 */
public class BasePluginScanner implements PluginScanner{

    private PathResolve pathResolve;

    public void setPathResolve(PathResolve pathResolve) {
        this.pathResolve = pathResolve;
    }

    @Override
    public List<Path> scan(List<String> rootDir) {
        List<Path> pluginPaths = new ArrayList<>();
        for (String dir : rootDir) {
            if(ObjectUtils.isEmpty(dir)){
                continue;
            }
            File file = new File(dir);
            if(!file.exists()){
                continue;
            }
            resolve(file, pluginPaths);
        }
        return pluginPaths;
    }

    protected void resolve(File currentFile, List<Path> pluginPaths){
        if(currentFile == null || !currentFile.exists()){
            return;
        }
        if(pathResolve == null){
            return;
        }
        Path currentPath = currentFile.toPath();
        currentPath = pathResolve.resolve(currentPath);
        if(currentPath != null){
            pluginPaths.add(currentPath);
        } else {
            File[] files = currentFile.listFiles();
            if(files == null || files.length == 0){
                return;
            }
            for (File file : files) {
                resolve(file, pluginPaths);
            }
        }
    }
}
