package com.gitee.starblues.core.descriptor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 开发环境 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class DevPluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    @Override
    protected Properties getProperties(Path location) throws Exception {
        String bootstrapFilePath = location.toString() + File.separator + BOOTSTRAP_FILE_NAME;
        File file = new File(bootstrapFilePath);
        if(!file.exists()){
            return null;
        }
        Path path = Paths.get(bootstrapFilePath);
        try {
            return super.getProperties(Files.newInputStream(path));
        } finally {
            try {
                path.getFileSystem().close();
            } catch (Exception e) {
                // 忽略
            }
        }
    }

}
