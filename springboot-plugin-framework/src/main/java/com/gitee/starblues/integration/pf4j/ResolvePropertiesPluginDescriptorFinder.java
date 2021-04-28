package com.gitee.starblues.integration.pf4j;

import org.pf4j.PluginRuntimeException;
import org.pf4j.PropertiesPluginDescriptorFinder;
import org.pf4j.util.FileUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * 解决乱码问题
 *
 * @author starBlues
 * @version 2.4.0
 */
class ResolvePropertiesPluginDescriptorFinder extends PropertiesPluginDescriptorFinder {


    @Override
    protected Properties readProperties(Path pluginPath) {
        Path propertiesPath = getPropertiesPath(pluginPath, propertiesFileName);
        return getProperties(propertiesPath);
    }

    public static Properties getProperties(Path propertiesPath){
        if (propertiesPath == null) {
            throw new PluginRuntimeException("Cannot find the properties path");
        }


        if (Files.notExists(propertiesPath)) {
            throw new PluginRuntimeException("Cannot find '{}' path", propertiesPath);
        }

        Properties properties = new Properties();

        try (InputStreamReader input = new InputStreamReader(Files.newInputStream(propertiesPath),
                StandardCharsets.UTF_8)) {
            properties.load(input);
        } catch (IOException e) {
            throw new PluginRuntimeException(e);
        } finally {
            FileUtils.closePath(propertiesPath);
        }

        return properties;
    }
}