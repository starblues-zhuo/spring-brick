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

package com.gitee.starblues.utils;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 扫描工具类
 *
 * @author starBlues
 * @version 2.2.2
 */
public class ScanUtils {

    /**
     * 扫描指定包中的类。包括子包中的类
     *
     * @param basePackage 包名
     * @param baseClass  当前操作的基础类
     * @return 类全路径(形如：xx.bb.cc)
     * @throws IOException 扫描异常
     */
    public static Set<String> scanClassPackageName(String basePackage, Class baseClass) throws IOException {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            // windows
            return scanClassPackageNameOfWindows(basePackage, baseClass);
        } else {
            // unix or linux
            return scanClassPackageNameOfOther(basePackage, baseClass);
        }
    }

    /**
     * 得到扫描的包
     * @param pluginClass 插件入口class
     * @return 包集合
     */
    public static String[] getScanBasePackages(Class<?> pluginClass){
        SpringBootApplication springBootApplication = pluginClass.getAnnotation(SpringBootApplication.class);
        if(springBootApplication != null){
            String[] scanBasePackages = springBootApplication.scanBasePackages();
            if(scanBasePackages.length > 0){
                return scanBasePackages;
            }
        }
        return new String[]{ pluginClass.getPackage().getName() };
    }

    /**
     * 扫描windows环境下的类。包括子包中的类
     * @param basePackage 包名
     * @param baseClass 当前操作的基础类
     * @return 类全路径(形如：xx.bb.cc)
     * @throws IOException 扫描异常
     */
    private static Set<String> scanClassPackageNameOfWindows(String basePackage, Class baseClass) throws IOException {
        String classpathRootPath = baseClass.getResource("/").getPath();
        final String classpath = classpathRootPath
                .replace("/","\\")
                .replaceFirst("\\\\","");
        // 把包名 packageName 转换为路径名
        basePackage = basePackage.replace(".", File.separator);
        // class 文件全路径
        String fullPath = classpath + basePackage;

        return filterPath(fullPath).map(path -> {
                    String pathString = path.toString();
                    return pathString
                            .replace(classpath, "")
                            .replace("\\",".")
                            .replace(".class","");
                }).collect(Collectors.toSet());
    }


    /**
     * 扫描linux/unix/mac环境下的类。包括子包中的类
     * @param basePackage 包名
     * @param baseClass 当前操作的基础类
     * @return 类全路径(形如：xx.bb.cc)
     * @throws IOException 扫描异常
     */
    private static Set<String> scanClassPackageNameOfOther(String basePackage, Class baseClass) throws IOException {
        final String classpath = baseClass.getResource("/").getPath();
        // class 文件全路径
        String fullPath = classpath + ClassUtils.classPackageAsResourcePath(baseClass);

        return filterPath(fullPath).map(path -> {
                    String pathString = path.toString();
                    // 去头去尾
                    pathString = pathString
                            .replace(classpath, "")
                            .replace(".class", "");
                    // 转换为 aa.bb.cc
                    return ClassUtils.convertResourcePathToClassName(pathString);
                }).collect(Collectors.toSet());
    }

    /**
     * 过滤类
     * @param fullPath 类的全路径
     * @return Stream<Path>
     * @throws IOException IOException
     */
    private static Stream<Path> filterPath(String fullPath) throws IOException {
        return Files.walk(Paths.get(fullPath))
                .filter(Objects::nonNull)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    if(fileName == null){
                        return false;
                    }
                    return fileName.endsWith(".class");
                });
    }

}
