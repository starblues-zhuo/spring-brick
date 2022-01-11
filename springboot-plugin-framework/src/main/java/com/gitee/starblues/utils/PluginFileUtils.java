package com.gitee.starblues.utils;


import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 插件文件工具类
 *
 * @author starBlues
 * @version 3.0.0
 */
public final class PluginFileUtils {

    private static final String FILE_POINT = ".";

    private PluginFileUtils(){}


    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    public static void cleanEmptyFile(List<Path> paths){
        if(ObjectUtils.isEmpty(paths)){
            return;
        }
        for (Path path : paths) {
            if(!Files.exists(path)){
                continue;
            }
            try {
                Files.list(path)
                        .forEach(subPath -> {
                            File file = subPath.toFile();
                            if(!file.isFile()){
                                return;
                            }
                            long length = file.length();
                            if(length == 0){
                                try {
                                    Files.deleteIfExists(subPath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 如果文件不存在, 则会创建
     * @param path 插件路径
     * @return 插件路径
     * @throws IOException 没有发现文件异常
     */
    public static Path createExistFile(Path path) throws IOException {
        Path parent = path.getParent();
        if(!Files.exists(parent)){
            Files.createDirectories(parent);
        }
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        return path;
    }

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
     * 得到文件名称
     * @param fileName 原始文件名称. 比如: file.txt
     * @return String
     */
    public static String getFileName(String fileName){
        if(ObjectUtils.isEmpty(fileName)){
            return fileName;
        }
        if(fileName.lastIndexOf(FILE_POINT) > 0){
            return fileName.substring(0, fileName.lastIndexOf(FILE_POINT));
        } else {
            return fileName;
        }
    }

}
