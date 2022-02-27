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

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.archive.Archive;
import com.gitee.starblues.loader.archive.ExplodedArchive;
import com.gitee.starblues.loader.archive.JarFileArchive;
import com.gitee.starblues.loader.classloader.GenericClassLoader;
import com.gitee.starblues.loader.classloader.resource.loader.JarResourceLoader;
import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;

/**
 * 主程序jar in jar 模式启动者
 * @author starBlues
 * @version 3.0.0
 */
public class MainJarProgramLauncher extends MainProgramLauncher{

    private static final String PROD_CLASSES_PATH = "classes/";
    private static final String PROD_CLASSES_URL_SIGN = "/classes!/";

    private static final String PROD_LIB_PATH = "lib/";

    private final static Archive.EntryFilter ENTRY_FILTER = (entry)->{
        String name = entry.getName();
        return name.startsWith(PROD_CLASSES_PATH) || name.startsWith(PROD_LIB_PATH);
    };

    private final static Archive.EntryFilter INCLUDE_FILTER = (entry) -> {
        if (entry.isDirectory()) {
            return entry.getName().equals(PROD_CLASSES_PATH);
        }
        return entry.getName().startsWith(PROD_LIB_PATH);
    };

    private final File rootJarFile;

    public MainJarProgramLauncher(MethodRunner methodRunner, File rootJarFile) {
        super(methodRunner);
        this.rootJarFile = Objects.requireNonNull(rootJarFile, "参数 rootJarFile 不能为空");
    }

    @Override
    protected void addResource(GenericClassLoader classLoader) throws Exception {
        super.addResource(classLoader);
        Archive archive = getArchive();
        Iterator<Archive> archiveIterator = archive.getNestedArchives(ENTRY_FILTER, INCLUDE_FILTER);
        addLibResource(archiveIterator, classLoader);
    }

    private Archive getArchive() throws IOException {
        return (rootJarFile.isDirectory() ? new ExplodedArchive(rootJarFile) : new JarFileArchive(rootJarFile));
    }

    private void addLibResource(Iterator<Archive> archives, GenericClassLoader classLoader) throws Exception {
        while (archives.hasNext()){
            Archive archive = archives.next();
            URL url = archive.getUrl();
            String path = url.getPath();
            if(path.contains(PROD_CLASSES_URL_SIGN)){
                classLoader.addResource(new MainJarResourceLoader(url));
            } else {
                classLoader.addResource(new JarResourceLoader(url));
            }
        }
    }

    private static class MainJarResourceLoader extends JarResourceLoader {

        public MainJarResourceLoader(URL url) throws Exception {
            super(url);
        }

        @Override
        protected String resolveName(String name) {
            return name.replace(PROD_CLASSES_PATH, "");
        }
    }


}
