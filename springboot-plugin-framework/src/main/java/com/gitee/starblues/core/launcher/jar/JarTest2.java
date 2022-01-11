package com.gitee.starblues.core.launcher.jar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author starBlues
 * @version 1.0
 */
public class JarTest2 {

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\etc\\kitte\\ksm\\springboot-plugin-framework-parent\\springboot-plugin-framework-example\\example-plugins-basic\\example-basic-1\\target\\example-basic-1-1.0.0-SNAPSHOT_0.jar");
        File file2 = new File("D:\\etc\\kitte\\ksm\\springboot-plugin-framework-parent\\springboot-plugin-framework-example\\example-plugins-basic\\example-basic-1\\target\\plugin\\lib\\aspectjweaver-1.9.6.jar");
        JarInputStream jarStream = new JarInputStream(new BufferedInputStream(new FileInputStream(file2)));
        JarEntry jarEntry = null;
        while ((jarEntry = jarStream.getNextJarEntry()) != null) {
            System.out.println(jarEntry.getName());
        }
    }

}
