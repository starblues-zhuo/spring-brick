/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core.scanner;

import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
        if(ObjectUtils.isEmpty(rootDir)){
            return Collections.emptyList();
        }
        List<Path> pluginPaths = new ArrayList<>();
        if(pathResolve == null){
            return pluginPaths;
        }
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
