package com.gitee.starblues.core.descriptor;


import com.gitee.starblues.core.PluginException;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PluginDescriptorKey.*;
import static com.gitee.starblues.common.PackageStructure.*;
import static com.gitee.starblues.common.utils.ManifestUtils.*;

/**
 * 抽象的 PluginDescriptorLoader
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public PluginDescriptor load(Path location) throws PluginException {
        Manifest manifest = null;
        try {
            manifest = getManifest(location);
            if(manifest == null){
                logger.warn("路径[{}]没有发现[{}]", location, MANIFEST);
                return null;
            }
            return create(manifest, location);
        } catch (Exception e) {
            logger.warn("路径[{}]中存在非法[{}]: {}", location, MANIFEST, e.getMessage());
            return null;
        }
    }

    @Override
    public void close() throws Exception {

    }

    /**
     * 子类获取 Properties
     * @param location properties 路径
     * @return Properties
     * @throws Exception 异常
     */
    protected abstract Manifest getManifest(Path location) throws Exception;

    protected DefaultPluginDescriptor create(Manifest manifest, Path path) throws Exception{
        Attributes attributes = manifest.getMainAttributes();
        DefaultPluginDescriptor descriptor = new DefaultPluginDescriptor(
                getValue(attributes, PLUGIN_ID),
                getValue(attributes, PLUGIN_VERSION),
                getValue(attributes, PLUGIN_BOOTSTRAP_CLASS),
                path
        );
        descriptor.setManifest(manifest);
        descriptor.setPluginClassPath(getValue(attributes, PLUGIN_PATH, false));
        descriptor.setPluginLibPath(getPluginLibPaths(path, attributes));
        descriptor.setDescription(getValue(attributes, PLUGIN_DESCRIPTION, false));
        descriptor.setRequires(getValue(attributes, PLUGIN_REQUIRES, false));
        descriptor.setProvider(getValue(attributes, PLUGIN_PROVIDER, false));
        descriptor.setLicense(getValue(attributes, PLUGIN_LICENSE, false));
        descriptor.setConfigFileName(getValue(attributes, PLUGIN_CONFIG_FILE_NAME, false));
        return descriptor;
    }


    protected Set<String> getPluginLibPaths(Path path, Attributes attributes) throws Exception{
        String libIndex = getValue(attributes, PLUGIN_LIB_INDEX);
        if(ObjectUtils.isEmpty(libIndex)){
            return Collections.emptySet();
        }
        File file = new File(libIndex);
        if(!file.exists()){
            // 如果绝对路径不存在, 则判断相对路径
            String pluginPath = getValue(attributes, PLUGIN_PATH);
            file = new File(CommonUtils.joiningFilePath(pluginPath, libIndex));
        }
        if(!file.exists()){
            // 都不存在, 则返回为空
            return Collections.emptySet();
        }
        try {
            List<String> paths = Files.readAllLines(file.toPath());
            if(!ObjectUtils.isEmpty(paths)){
                return new HashSet<>(paths);
            }
        } catch (IOException e) {
            throw new Exception("Load plugin lib index path failure. " + libIndex, e);
        }
        return Collections.emptySet();
    }

    protected Manifest getManifest(InputStream inputStream) throws Exception{
        Manifest manifest = new Manifest();
        try {
            manifest.read(inputStream);
            return manifest;
        } finally {
            inputStream.close();
        }
    }

}
