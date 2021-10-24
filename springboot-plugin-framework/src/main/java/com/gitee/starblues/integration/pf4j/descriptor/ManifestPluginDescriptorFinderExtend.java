package com.gitee.starblues.integration.pf4j.descriptor;

import org.pf4j.DefaultPluginDescriptor;
import org.pf4j.ManifestPluginDescriptorFinder;
import org.pf4j.PluginDescriptor;
import org.pf4j.util.StringUtils;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * 扩展 ManifestPluginDescriptorFinderExtend
 * @author starBlues
 * @version 2.4.5
 */
public class ManifestPluginDescriptorFinderExtend extends ManifestPluginDescriptorFinder {

    public static final String PLUGIN_CONFIG_FILE_NAME = "Plugin-ConfigFileName";
    public static final String PLUGIN_CONFIG_FILE_PROFILE = "Plugin-ConfigFileProfile";


    @Override
    protected PluginDescriptor createPluginDescriptor(Manifest manifest) {
        DefaultPluginDescriptorExtend pluginDescriptor = (DefaultPluginDescriptorExtend)
                super.createPluginDescriptor(manifest);
        Attributes attributes = manifest.getMainAttributes();
        String configFileName = attributes.getValue(PLUGIN_CONFIG_FILE_NAME);
        if (StringUtils.isNullOrEmpty(configFileName)) {
            pluginDescriptor.setConfigFileName(configFileName);
        }
        String configFileProfile = attributes.getValue(PLUGIN_CONFIG_FILE_PROFILE);
        if (!StringUtils.isNullOrEmpty(configFileProfile)) {
            pluginDescriptor.setConfigFileProfile(configFileProfile);
        }
        return pluginDescriptor;
    }

    @Override
    protected DefaultPluginDescriptor createPluginDescriptorInstance() {
        return new DefaultPluginDescriptorExtend();
    }
}
