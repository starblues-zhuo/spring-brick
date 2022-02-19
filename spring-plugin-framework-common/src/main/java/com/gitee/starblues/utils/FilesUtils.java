package com.gitee.starblues.utils;

import java.io.File;
import java.util.function.Supplier;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class FilesUtils {

    public static File getExistFile(String pathStr){
        File file = new File(pathStr);
        if(file.exists()){
            return file;
        }
        return null;
    }

    public static File getExistFile(String pathStr, Supplier<String> secondPathSupplier){
        File existFile = getExistFile(pathStr);
        if(existFile != null){
            return existFile;
        }
        return getExistFile(secondPathSupplier.get());
    }

    /**
     * rest接口拼接路径
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

}
