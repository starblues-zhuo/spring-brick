package com.gitee.starblues.core.descriptor;


import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.utils.ManifestUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PluginDescriptorKey.PLUGIN_RESOURCES_CONFIG;


/**
 * 生产环境打包好的插件 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPackagePluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    private final PluginDescriptor.Type type;
    private PluginResourcesConfig pluginResourcesConfig;

    public ProdPackagePluginDescriptorLoader(PluginDescriptor.Type type) {
        this.type = type;
    }

    @Override
    protected Manifest getManifest(Path location) throws Exception {
        try (JarFile jarFile = new JarFile(location.toFile())){
            JarEntry jarEntry = jarFile.getJarEntry(PackageStructure.PROD_MANIFEST_PATH);
            // TODO
            InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
            Manifest manifest = PluginFileUtils.getManifest(jarFileInputStream);
            jarFileInputStream.close();
            pluginResourcesConfig = getPluginResourcesConfig(jarFile, manifest);
            return manifest;
        }
    }

    @Override
    protected PluginResourcesConfig getPluginResourcesConfig(Path path, Attributes attributes) throws Exception {
        return pluginResourcesConfig;
    }

    @Override
    protected DefaultInsidePluginDescriptor create(Manifest manifest, Path path) throws Exception {
        DefaultInsidePluginDescriptor descriptor = super.create(manifest, path);
        descriptor.setType(type);
        return descriptor;
    }

    protected PluginResourcesConfig getPluginResourcesConfig(JarFile jarFile, Manifest manifest) throws Exception {
        Attributes attributes = manifest.getMainAttributes();
        String pluginResourcesConf = ManifestUtils.getValue(attributes, PLUGIN_RESOURCES_CONFIG);
        if(ObjectUtils.isEmpty(pluginResourcesConf)){
            return new PluginResourcesConfig();
        }
        JarEntry jarEntry = jarFile.getJarEntry(pluginResourcesConf);
        if(jarEntry == null){
            return new PluginResourcesConfig();
        }
        InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
        List<String> lines = IOUtils.readLines(jarFileInputStream, PackageStructure.CHARSET_NAME);
        return PluginResourcesConfig.parse(lines);
    }

}
