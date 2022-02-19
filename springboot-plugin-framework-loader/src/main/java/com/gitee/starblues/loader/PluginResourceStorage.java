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

package com.gitee.starblues.loader;

import com.gitee.starblues.loader.jar.AbstractJarFile;
import com.gitee.starblues.loader.jar.JarFile;
import com.gitee.starblues.loader.jar.JarFileWrapper;
import com.gitee.starblues.loader.utils.IOUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件资源存储者
 * @author starBlues
 * @version 3.0.0
 */
public class PluginResourceStorage {

    public final static Map<String, Storage> STORAGES = new ConcurrentHashMap<>();


    public static void addPlugin(String pluginId, String pluginFileName){
        if(STORAGES.containsKey(pluginId)){
            return;
        }
        STORAGES.put(pluginId, new Storage(pluginFileName));
    }


    public static void removePlugin(String pluginId){
        Storage storage = STORAGES.get(pluginId);
        if(storage == null){
            return;
        }
        IOUtils.closeQuietly(storage);
        STORAGES.remove(pluginId);
    }

    public static void addJarFile(AbstractJarFile jarFile){
        STORAGES.forEach((k,v)->{
            v.addJarFile(jarFile.getName(), jarFile);
        });
    }

    public static void addRootJarFile(File file, JarFile jarFile){
        STORAGES.forEach((k,v)->{
            v.addRootJarFile(file, jarFile);
        });
    }

    public static JarFile getRootJarFile(File file){
        for (Storage value : STORAGES.values()) {
            JarFile jarFile = value.getRootJarFile(file);
            if(jarFile != null){
                return jarFile;
            }
        }
        return null;
    }


    private static class Storage implements Closeable {
        private final String pluginFileName;
        private final Map<File, JarFile> rootJarFileMap = new ConcurrentHashMap<>();
        private final Map<String, List<AbstractJarFile>> jarFileMap = new ConcurrentHashMap<>();

        public Storage(String pluginFileName) {
            this.pluginFileName = pluginFileName;
        }

        public void addJarFile(String name, AbstractJarFile jarFile){
            if(name == null || jarFile == null){
                return;
            }
            if(name.contains(pluginFileName)){
                List<AbstractJarFile> jarFiles = jarFileMap.computeIfAbsent(name, k -> new ArrayList<>());
                jarFiles.add(jarFile);
            }
        }

        public void addRootJarFile(File file, JarFile jarFile){
            String absolutePath = file.getAbsolutePath();
            if(absolutePath.contains(pluginFileName)){
                rootJarFileMap.put(file, jarFile);
            }
        }

        public JarFile getRootJarFile(File file){
            return rootJarFileMap.get(file);
        }

        @Override
        public void close() throws IOException {
            for (List<AbstractJarFile> value : jarFileMap.values()) {
                for (AbstractJarFile jarFile : value) {
                    if(jarFile == null){
                        continue;
                    }
                    if(jarFile instanceof JarFileWrapper){
                        ((JarFileWrapper)jarFile).canClosed();
                    }
                    IOUtils.closeQuietly(jarFile);
                }
            }
            jarFileMap.clear();
            rootJarFileMap.clear();
        }
    }


}
