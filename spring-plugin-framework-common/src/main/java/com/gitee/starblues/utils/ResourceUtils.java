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


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 资源工具类
 *
 * @author starBlues
 * @version 3.0.0
 */
public class ResourceUtils {

    public static final String URL_PROTOCOL_FILE = "file";

    public final static String TYPE_FILE = "file";
    public final static String TYPE_CLASSPATH = "classpath";
    public final static String TYPE_PACKAGE = "package";

    public static final String ROOT_PLUGIN_SIGN = "~";

    public final static String TYPE_SPLIT = ":";

    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";

    public static final String PACKAGE_SPLIT = "/";
    public static final String PACKAGE_SPLIT_DOT = ".";

    public static final String JAR_FILE_EXTENSION = ".jar";
    public static final String ZIP_FILE_EXTENSION = ".zip";
    public static final String CLASS_FILE_EXTENSION = ".class";
    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
    public static final String URL_PROTOCOL_VFS = "vfs";

    /**
     * 获取匹配路绝
     * @param locationMatch 原始匹配路径。规则为: file:xxx, classpath:xxx , package:xxx
     * @return 整合出完整的匹配路绝
     */
    public static String getMatchLocation(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return null;
        }
        String classPathType = TYPE_CLASSPATH + TYPE_SPLIT;
        if(isClasspath(locationMatch)){
            return locationMatch.replaceFirst(classPathType, "");
        }
        String fileType = TYPE_FILE + TYPE_SPLIT;
        if(isFile(locationMatch)){
            return locationMatch.replaceFirst(fileType, "");
        }
        String packageType = TYPE_PACKAGE + TYPE_SPLIT;
        if(isPackage(locationMatch)){
            String location = locationMatch.replaceFirst(packageType, "");
            return replacePackage(location);
        }
        return null;
    }

    public static boolean isClasspath(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return false;
        }
        return locationMatch.startsWith(TYPE_CLASSPATH + TYPE_SPLIT);
    }

    public static boolean isFile(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return false;
        }
        return locationMatch.startsWith(TYPE_FILE + TYPE_SPLIT);
    }

    public static boolean isPackage(String locationMatch){
        if(ObjectUtils.isEmpty(locationMatch)){
            return false;
        }
        return locationMatch.startsWith(TYPE_PACKAGE + TYPE_SPLIT);
    }

    public static boolean isClass(String path){
        if(ObjectUtils.isEmpty(path)){
            return false;
        }
        return path.toLowerCase().endsWith(CLASS_FILE_EXTENSION);
    }

    /**
     * 是否为 zip 文件
     * @param path 文件路径
     * @return boolean
     */
    public static boolean isZipFile(Path path) {
        return Files.isRegularFile(path) && isZip(path.toString());
    }

    /**
     * 是否为 jar 文件
     * @param path 文件路径
     * @return boolean
     */
    public static boolean isJarFile(Path path) {
        return Files.isRegularFile(path) && isJar(path.toString());
    }

    /**
     * 是否为 zip 文件
     * @param name 文件名称
     * @return boolean
     */
    public static boolean isZip(String name) {
        return name != null && name.toLowerCase().endsWith(ZIP_FILE_EXTENSION);
    }

    /**
     * 是否为 jar 文件
     * @param name 文件名称
     * @return boolean
     */
    public static boolean isJar(String name) {
        return name != null && name.toLowerCase().endsWith(JAR_FILE_EXTENSION);
    }

    public static boolean isDirFile(Path path) {
        return path.toFile().isDirectory();
    }

    public static boolean isJarFileURL(URL url) {
        return (URL_PROTOCOL_FILE.equals(url.getProtocol()) ||
                url.getPath().toLowerCase().endsWith(JAR_FILE_EXTENSION));
    }

    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }


    public static boolean existFile(File rootFile, String checkFileName){
        if(rootFile == null || !rootFile.exists()){
            return false;
        }
        final File[] listFiles = rootFile.listFiles();
        if(listFiles == null || listFiles.length == 0){
            return false;
        }
        for (File listFile : listFiles) {
            if(Objects.equals(listFile.getName(), checkFileName)){
                return true;
            }
            if(listFile.isDirectory()){
                return existFile(listFile, checkFileName);
            }
        }
        return false;
    }

    public static void listFile(File rootFile, Consumer<File> consumerFile){
        if(rootFile == null || !rootFile.exists()){
            return;
        }
        final File[] listFiles = rootFile.listFiles();
        if(listFiles == null || listFiles.length == 0){
            return;
        }
        for (File listFile : listFiles) {
            if(listFile.isDirectory()){
                listFile(listFile, consumerFile);
                continue;
            }
            consumerFile.accept(listFile);
        }
    }

    public static String replacePackage(String packageName){
        if(packageName == null){
            return null;
        }
        return packageName.replace(PACKAGE_SPLIT_DOT, PACKAGE_SPLIT);
    }


    public static boolean isUrl(String resourceLocation) {
        if (resourceLocation == null) {
            return false;
        }
        if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
            return true;
        }
        try {
            new URL(resourceLocation);
            return true;
        }
        catch (MalformedURLException ex) {
            return false;
        }
    }

    public static void closeQuietly(Object closeable){
        closeQuietly(closeable, null);
    }

    public static void closeQuietly(Object closeable, Consumer<Exception> consumer){
        if (closeable != null) {
            try {
                if(closeable instanceof AutoCloseable){
                    ((AutoCloseable) closeable).close();
                }
            } catch (final Exception e) {
                if (consumer != null) {
                    consumer.accept(e);
                }
            }
        }
    }

    public static String getFileSuffix(String fileName){
        if(fileName == null){
            return null;
        }
        int i = fileName.lastIndexOf(".");
        if(i > 0 && fileName.length() >= (i + 1)){
            return fileName.substring(i + 1);
        } else {
            return "";
        }
    }

}
