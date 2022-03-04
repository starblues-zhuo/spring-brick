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


import com.gitee.starblues.common.PackageStructure;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
            IOUtils.closeQuietly(in);
        }
        return value;
    }


    public static void cleanEmptyFile(List<String> paths){
        if(ObjectUtils.isEmpty(paths)){
            return;
        }
        for (String pathStr : paths) {
            Path path = Paths.get(pathStr);
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
    public static File createExistFile(Path path) throws IOException {
        Path parent = path.getParent();
        if(!Files.exists(parent)){
            Files.createDirectories(parent);
        }
        if(!Files.exists(path)){
            Files.createFile(path);
        }
        return path.toFile();
    }

    /**
     * 得到文件名称
     * @param file 原始文件
     * @return String
     */
    public static String getFileName(File file){
        String fileName = file.getName();
        if(!file.exists() | file.isDirectory()){
            return fileName;
        }
        return getFileName(fileName);
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

    public static Manifest getManifest(InputStream inputStream) throws IOException {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        List<String> lines = IOUtils.readLines(inputStream, PackageStructure.CHARSET_NAME);
        for (String line : lines) {
            String[] split = line.split(":");
            if(split.length == 2){
                String key = split[0];
                String value = split[1];
                attributes.putValue(trim(key), trim(value));
            }
        }
        return manifest;
    }

    private static String trim(String value){
        if(ObjectUtils.isEmpty(value)){
            return value;
        }
        return value.trim();
    }

    public static void deleteFile(File file) throws IOException {
        if(file == null || !file.exists()){
            return;
        }
        if(file.isDirectory()){
            FileUtils.deleteDirectory(file);
        } else {
            FileUtils.delete(file);
        }
    }

    public static void decompressZip(String zipPath, String targetDir) throws IOException {
        File zipFile = new File(zipPath);
        if(!ResourceUtils.isZip(zipPath) && !ResourceUtils.isJar(zipPath)){
            throw new IOException("文件[" + zipFile.getName() + "]非压缩包, 不能解压");
        }
        File targetDirFile = new File(targetDir);
        if(!targetDirFile.exists()){
            targetDirFile.mkdirs();
        }
        try (ZipFile zip = new ZipFile(zipFile, Charset.forName(PackageStructure.CHARSET_NAME))) {
            Enumeration<? extends ZipEntry> zipEnumeration = zip.entries();
            ZipEntry zipEntry = null;
            while (zipEnumeration.hasMoreElements()) {
                zipEntry = zipEnumeration.nextElement();
                String zipEntryName = zipEntry.getName();
                String currentZipPath = PackageStructure.resolvePath(zipEntryName);
                String currentTargetPath = FilesUtils.joiningFilePath(targetDir, currentZipPath);
                //判断路径是否存在,不存在则创建文件路径
                if (zipEntry.isDirectory()) {
                    FileUtils.forceMkdir(new File(currentTargetPath));
                    continue;
                }
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    in = zip.getInputStream(zipEntry);
                    out = new FileOutputStream(currentTargetPath);
                    IOUtils.copy(in, out);
                } finally {
                    if (in != null) {
                        IOUtils.closeQuietly(in);
                    }
                    if (out != null) {
                        IOUtils.closeQuietly(out);
                    }
                }
            }
        }
    }

}
