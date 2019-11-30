package com.gitee.starblues.utils;


import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 扫描工具类
 *
 * @author zhangzhuo
 * @version 2.2.2
 */
public class ScanUtils {

    /**
     * 扫描指定包中的类。包括子包中的类
     * @param basePackage 包名
     * @param baseClass 当前操作的基础类
     * @return 类全路径
     * @throws IOException 扫描异常
     */
    public static Set<String> scanClassPackageName(String basePackage, Class baseClass) throws IOException {
        String classpathRootPath = baseClass.getResource("/").getPath();
        final String classpath = classpathRootPath
                .replace("/","\\")
                .replaceFirst("\\\\","");
        // 把包名 packageName 转换为路径名
        basePackage = basePackage.replace(".", File.separator);
        // class 文件全路径
        String fullPath = classpath + basePackage;

        return Files.walk(Paths.get(fullPath))
                .filter(path -> path != null)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    if(fileName == null){
                        return false;
                    }
                    return fileName.endsWith(".class");
                })
                .map(path -> {
                    String pathString = path.toString();
                    return pathString
                            .replace(classpath, "")
                            .replace("\\",".")
                            .replace(".class","");
                }).collect(Collectors.toSet());
    }


    /**
     * 扫描jar包中的类。
     * @param basePackage 包名
     * @param classLoader jar的ClassLoader
     * @return 类全路径
     * @throws IOException 扫描异常
     */
    public static Set<String> scanClassPackageName(String basePackage, ClassLoader classLoader) throws IOException {
        Enumeration<URL> urlEnumeration = classLoader.getResources(basePackage.replace(".", "/"));
        Set<String> classPackageNames = new HashSet<>();
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            String protocol = url.getProtocol();
            if (!"jar".equalsIgnoreCase(protocol)) {
                // 不是jar协议
                return classPackageNames;
            }
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            if (connection == null) {
                return classPackageNames;
            }
            JarFile jarFile = connection.getJarFile();
            if (jarFile == null) {
                return classPackageNames;
            }
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            // 迭代
            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry entry = jarEntryEnumeration.nextElement();
                String jarEntryName = entry.getName();
                if (jarEntryName.contains(".class") &&
                        jarEntryName.replaceAll("/",".").startsWith(basePackage)) {
                    String className = jarEntryName
                            .substring(0, jarEntryName.lastIndexOf("."))
                            .replace("/", ".");
                    classPackageNames.add(className);
                }
            }
        }
        return classPackageNames;
    }


}
