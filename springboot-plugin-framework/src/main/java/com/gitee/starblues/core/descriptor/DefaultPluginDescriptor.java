package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.utils.Assert;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PluginDescriptorKey.*;

/**
 * 默认插件描述者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginDescriptor implements PluginDescriptor{

    private final String pluginId;
    private final String pluginVersion;
    private final String pluginBootstrapClass;
    private final String pluginPath;


    private Type type;
    private String description;
    private String requires;
    private String provider;
    private String license;


    public DefaultPluginDescriptor(String pluginId, String pluginVersion,
                                   String pluginClass, String pluginPath) {
        this.pluginId = Assert.isNotEmpty(pluginId, PLUGIN_ID + "不能为空");
        this.pluginVersion = Assert.isNotEmpty(pluginVersion, PLUGIN_VERSION + "不能为空");
        this.pluginBootstrapClass = Assert.isNotEmpty(pluginClass, PLUGIN_BOOTSTRAP_CLASS + "不能为空");
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

    void setType(Type type) {
        this.type = type;
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
    public String getPluginBootstrapClass() {
        return pluginBootstrapClass;
    }

    @Override
    public String getPluginPath() {
        return pluginPath;
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
    public Type getType() {
        return type;
    }

}
