/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.utils;

import com.gitee.starblues.common.Constants;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * 文件工具类
 *
 * @author starBlues
 * @version 3.0.0
 */
public class FilesUtils {

    /**
     * 获取存在的文件
     *
     * @param pathStr 文件路径
     * @return File
     */
    public static File getExistFile(String pathStr){
        File file = new File(pathStr);
        if(file.exists()){
            return file;
        }
        return null;
    }


    /**
     * rest接口拼接路径
     *
     * @param path1 路径1
     * @param path2 路径2
     * @return 拼接的路径
     */
    public static String restJoiningPath(String path1, String path2){
        if(path1 != null && path2 != null){
            if(path1.endsWith("/") && path2.startsWith("/")){
                return path1 + path2.substring(1);
            } else if(!path1.endsWith("/") && !path2.startsWith("/")){
                return path1 + "/" + path2;
            } else {
                return path1 + path2;
            }
        } else if(path1 != null){
            return path1;
        } else if(path2 != null){
            return path2;
        } else {
            return "";
        }
    }


    /**
     * 拼接url路径
     *
     * @param paths 拼接的路径
     * @return 拼接的路径
     */
    public static String joiningUrlPath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(ObjectUtils.isEmpty(path)) {
                continue;
            }
            if((i < length - 1) && path.endsWith("/")){
                path = path.substring(path.lastIndexOf("/"));
            }
            if(path.startsWith("/")){
                stringBuilder.append(path);
            } else {
                stringBuilder.append("/").append(path);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 拼接file路径
     *
     * @param paths 拼接的路径
     * @return 拼接的路径
     */
    public static String joiningFilePath(String ...paths){
        if(paths == null || paths.length == 0){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int length = paths.length;
        for (int i = 0; i < length; i++) {
            String path = paths[i];
            if(ObjectUtils.isEmpty(path)) {
                continue;
            }
            if(i > 0){
                if(path.startsWith(File.separator) || path.startsWith("/") ||
                        path.startsWith("\\") || path.startsWith("//")){
                    stringBuilder.append(path);
                } else {
                    stringBuilder.append(File.separator).append(path);
                }
            } else {
                stringBuilder.append(path);
            }
        }

        return stringBuilder.toString();
    }

    public static File createFile(String path) throws IOException {
        try {
            File file = new File(path);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()){
                if(!parentFile.mkdirs()){
                    throw new IOException("Create " + parentFile + " dir error");
                }
            }
            if(file.createNewFile()){
                return file;
            }
            throw new IOException("Create " + path + " file error");
        } catch (Exception e){
            throw new IOException("Create " + path + " file error");
        }
    }


    /**
     * 解决相对路径
     * @param rootPath 根路径
     * @param relativePath 以 ~ 开头的相对路径
     * @return 处理后的路径
     */
    public static String resolveRelativePath(String rootPath, String relativePath){
        if(ObjectUtils.isEmpty(relativePath)){
            return relativePath;
        }
        if(relativePath.startsWith(Constants.RELATIVE_SIGN)){
            return joiningFilePath(rootPath, relativePath.replaceFirst(Constants.RELATIVE_SIGN, ""));
        } else {
            return relativePath;
        }
    }

}
