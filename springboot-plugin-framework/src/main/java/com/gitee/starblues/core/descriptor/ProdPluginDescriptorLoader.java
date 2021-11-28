package com.gitee.starblues.core.descriptor;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 生产环境 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    @Override
    protected Properties getProperties(Path location) throws Exception {
        JarFile jarFile = new JarFile(location.toFile());
        JarEntry jarEntry = jarFile.getJarEntry(BOOTSTRAP_FILE_NAME);
        if(jarEntry == null){
            return null;
        }
        try {
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            return getProperties(inputStream);
        } finally {
            jarFile.close();
        }
    }

}
