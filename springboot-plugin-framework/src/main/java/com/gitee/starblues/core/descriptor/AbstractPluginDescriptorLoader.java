package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.PluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 抽象的 PluginDescriptorLoader
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public PluginDescriptor load(Path location) throws PluginException {
        Properties properties = null;
        try {
            properties = getProperties(location);
            if(properties == null){
                logger.warn("路径[{}]没有发现引导文件[{}]", location, BOOTSTRAP_FILE_NAME);
                return null;
            }
            return create(properties, location);
        } catch (Exception e) {
            throw new PluginException("获取解析[" + location + "]插件引导文件失败." + e.getMessage(), e);
        }
    }

    /**
     * 子类获取 Properties
     * @param location properties 路径
     * @return Properties
     * @throws Exception 异常
     */
    protected abstract Properties getProperties(Path location) throws Exception;

    protected PluginDescriptor create(Properties properties, Path path){
        DefaultPluginDescriptor descriptor = new DefaultPluginDescriptor(
                properties.getProperty(PLUGIN_ID),
                properties.getProperty(PLUGIN_VERSION),
                properties.getProperty(PLUGIN_CLASS),
                path
        );
        descriptor.setPluginLibDir(properties.getProperty(PLUGIN_LIB_DIR));
        descriptor.setDescription(properties.getProperty(PLUGIN_DESCRIPTION));
        descriptor.setRequires(properties.getProperty(PLUGIN_REQUIRES));
        descriptor.setProvider(properties.getProperty(PLUGIN_PROVIDER));
        descriptor.setLicense(properties.getProperty(PLUGIN_LICENSE));
        descriptor.setConfigFileName(properties.getProperty(PLUGIN_CONFIG_FILE_NAME));
        return descriptor;
    }


    protected Properties getProperties(InputStream inputStream) throws Exception{
        Properties properties = new Properties();
        try (InputStreamReader input = new InputStreamReader(inputStream,
                StandardCharsets.UTF_8)) {
            properties.load(input);
        }
        return properties;
    }

}
