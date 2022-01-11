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
    private final Path pluginPath;

    private String pluginClassPath;
    private Manifest manifest;
    private Type type;

    private Set<String> pluginLibPaths;
    private String description;
    private String requires;
    private String provider;
    private String license;

    private String configFileName;

    public DefaultPluginDescriptor(String pluginId, String pluginVersion,
                                   String pluginClass, Path pluginPath) {
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

    void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    void setPluginClassPath(String pluginClassPath) {
        this.pluginClassPath = pluginClassPath;
    }

    void setManifest(Manifest manifest){
        this.manifest = manifest;
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
    public Path getPluginPath() {
        return pluginPath;
    }

    @Override
    public String getPluginClassPath() {
        return pluginClassPath;
    }

    @Override
    public Set<String> getPluginLibPaths() {
        return pluginLibPaths;
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
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getConfigFileName() {
        return configFileName;
    }

    public void setPluginLibPath(Set<String> pluginLibPaths) {
        this.pluginLibPaths = pluginLibPaths;
    }
}
