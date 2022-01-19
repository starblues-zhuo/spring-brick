package com.gitee.starblues.core.descriptor;

import java.nio.file.Path;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * 内部的默认插件描述者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultInsidePluginDescriptor extends DefaultPluginDescriptor implements InsidePluginDescriptor{

    private final Path pluginPath;
    private final String pluginFileName;

    private String pluginClassPath;
    private Manifest manifest;
    private String configFileName;
    private Set<String> pluginLibPaths;
    private Set<String> includeMainResourcePatterns;
    private Set<String> excludeMainResourcePatterns;

    public DefaultInsidePluginDescriptor(String pluginId, String pluginVersion, String pluginClass, Path pluginPath) {
        super(pluginId, pluginVersion, pluginClass, pluginPath.toAbsolutePath().toString());
        this.pluginPath = pluginPath;
        this.pluginFileName = pluginPath.toFile().getName();
    }

    public void setPluginClassPath(String pluginClassPath) {
        this.pluginClassPath = pluginClassPath;
    }

    public void setManifest(Manifest manifest){
        this.manifest = manifest;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public void setPluginLibPath(Set<String> pluginLibPaths) {
        this.pluginLibPaths = pluginLibPaths;
    }

    public void setIncludeMainResourcePatterns(Set<String> includeMainResourcePatterns) {
        this.includeMainResourcePatterns = includeMainResourcePatterns;
    }

    public void setExcludeMainResourcePatterns(Set<String> excludeMainResourcePatterns) {
        this.excludeMainResourcePatterns = excludeMainResourcePatterns;
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
    public Set<String> getIncludeMainResourcePatterns() {
        return includeMainResourcePatterns;
    }

    @Override
    public Set<String> getExcludeMainResourcePatterns() {
        return excludeMainResourcePatterns;
    }

    @Override
    public String getConfigFileName() {
        return configFileName;
    }

    @Override
    public Path getInsidePluginPath() {
        return pluginPath;
    }

    @Override
    public String getPluginFileName() {
        return pluginFileName;
    }

    @Override
    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public PluginDescriptor toPluginDescriptor() {
        Path pluginPath = getInsidePluginPath();
        if(getType() == Type.DEV) {
            // dev模式 插件路径展示项目目录
            pluginPath = pluginPath.getParent().getParent();
        }
        DefaultPluginDescriptor descriptor = new DefaultPluginDescriptor(
                getPluginId(), getPluginVersion(), getPluginBootstrapClass(), pluginPath.toAbsolutePath().toString()
        );
        descriptor.setType(getType());
        descriptor.setDescription(getDescription());
        descriptor.setProvider(getProvider());
        descriptor.setRequires(getRequires());
        descriptor.setLicense(getLicense());
        return descriptor;
    }

}
