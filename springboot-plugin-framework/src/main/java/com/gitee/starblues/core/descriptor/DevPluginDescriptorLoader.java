package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.common.utils.ManifestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * 开发环境 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class DevPluginDescriptorLoader extends AbstractPluginDescriptorLoader{


    @Override
    protected Manifest getManifest(Path location) throws Exception {
        String manifestPath = location.toString() + File.separator + PackageStructure.MANIFEST;
        File file = new File(manifestPath);
        if(!file.exists()){
            return null;
        }
        Path path = Paths.get(manifestPath);
        try {
            return super.getManifest(Files.newInputStream(path));
        } finally {
            try {
                path.getFileSystem().close();
            } catch (Exception e) {
                // 忽略
            }
        }
    }

    @Override
    protected DefaultPluginDescriptor create(Manifest manifest, Path path) throws Exception {
        final DefaultPluginDescriptor descriptor = super.create(manifest, path);
        descriptor.setType(PluginDescriptor.Type.DIR_OF_DEV);
        return descriptor;
    }
}
