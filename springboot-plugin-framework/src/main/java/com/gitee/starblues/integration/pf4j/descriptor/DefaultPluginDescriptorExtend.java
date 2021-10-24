package com.gitee.starblues.integration.pf4j.descriptor;

import org.pf4j.DefaultPluginDescriptor;

/**
 * 扩展 DefaultPluginDescriptor 的功能
 * @author starBlues
 * @version 2.4.5
 */
public class DefaultPluginDescriptorExtend extends DefaultPluginDescriptor implements PluginDescriptorExtend{


    private String configFileName;
    private String configFileProfile;

    public DefaultPluginDescriptorExtend() {
        super();
    }

    public DefaultPluginDescriptorExtend(String pluginId, String pluginDescription, String pluginClass,
                                         String version, String requires, String provider, String license) {
        super(pluginId, pluginDescription, pluginClass, version, requires, provider, license);
    }

    @Override
    public String getConfigFileName() {
        return configFileName;
    }

    @Override
    public String getConfigFileProfile() {
        return configFileProfile;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public void setConfigFileProfile(String configFileProfile) {
        this.configFileProfile = configFileProfile;
    }
}
