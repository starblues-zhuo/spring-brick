package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.utils.Assert;

import java.nio.file.Path;
import java.util.List;

import static com.gitee.starblues.core.descriptor.PluginDescriptorLoader.*;

/**
 * 默认插件描述者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginDescriptor implements PluginDescriptor{

    private final String pluginId;
    private final String pluginVersion;
    private final String pluginClass;
    private final Path pluginPath;

    private String pluginLibDir;
    private String description;
    private String requires;
    private String provider;
    private String license;

    private String configFileName;

    public DefaultPluginDescriptor(String pluginId, String pluginVersion,
                                   String pluginClass, Path pluginPath) {
        this.pluginId = Assert.isNotEmpty(pluginId, PLUGIN_ID + "不能为空");
        this.pluginVersion = Assert.isNotEmpty(pluginVersion, PLUGIN_VERSION + "不能为空");
        this.pluginClass = Assert.isNotEmpty(pluginClass, PLUGIN_CLASS + "不能为空");
        this.pluginPath = Assert.isNotNull(pluginPath, "插件路径[pluginPath]不能为空");
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setRequires(String requires) {
        this.requires = requires;
    }

    void setProvider(String provider) {
        this.provider = provider;
    }

    void setLicense(String license) {
        this.license = license;
    }

    void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public String getPluginVersion() {
        return pluginVersion;
    }

    @Override
    public String getPluginClass() {
        return pluginClass;
    }

    @Override
    public Path getPluginPath() {
        return pluginPath;
    }

    @Override
    public String getPluginLibDir() {
        return pluginLibDir;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getRequires() {
        return requires;
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getLicense() {
        return license;
    }

    @Override
    public List<PluginDependency> getPluginDependency() {
        return null;
    }

    @Override
    public String getConfigFileName() {
        return configFileName;
    }

    public void setPluginLibDir(String pluginLibDir) {
        this.pluginLibDir = pluginLibDir;
    }

}
