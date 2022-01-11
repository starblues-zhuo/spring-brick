package com.gitee.starblues.core.descriptor;


import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.utils.ManifestUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PluginDescriptorKey.PLUGIN_LIB_INDEX;


/**
 * 生产环境打包好的插件 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPackagePluginDescriptorLoader extends AbstractPluginDescriptorLoader{

    private final PluginDescriptor.Type type;
    private Set<String> pluginLibPaths;

    public ProdPackagePluginDescriptorLoader(PluginDescriptor.Type type) {
        this.type = type;
    }

    @Override
    protected Manifest getManifest(Path location) throws Exception {
        try (JarFile jarFile = new JarFile(location.toFile())){
            Manifest manifest = jarFile.getManifest();
            pluginLibPaths = getPluginLibPaths(jarFile, manifest);
            return manifest;
        }
    }

    @Override
    protected Set<String> getPluginLibPaths(Path path, Attributes attributes) throws Exception {
        return pluginLibPaths;
    }

    @Override
    protected DefaultPluginDescriptor create(Manifest manifest, Path path) throws Exception {
        final DefaultPluginDescriptor descriptor = super.create(manifest, path);
        descriptor.setType(type);
        return descriptor;
    }

    protected Set<String> getPluginLibPaths(JarFile jarFile, Manifest manifest) throws Exception {
        Attributes attributes = manifest.getMainAttributes();
        String pluginLibIndex = ManifestUtils.getValue(attributes, PLUGIN_LIB_INDEX);
        if(ObjectUtils.isEmpty(pluginLibIndex)){
            return Collections.emptySet();
        }
        JarEntry jarEntry = jarFile.getJarEntry(pluginLibIndex);
        if(jarEntry == null){
            return Collections.emptySet();
        }
        InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
        List<String> libPaths = IOUtils.readLines(jarFileInputStream, PackageStructure.CHARSET_NAME);
        if(ObjectUtils.isEmpty(libPaths)){
            return Collections.emptySet();
        }
        return new HashSet<>(libPaths);
    }

}
