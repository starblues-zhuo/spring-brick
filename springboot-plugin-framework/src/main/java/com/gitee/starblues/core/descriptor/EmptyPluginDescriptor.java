package com.gitee.starblues.core.descriptor;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyPluginDescriptor implements InsidePluginDescriptor{
    @Override
    public String getPluginId() {
        return null;
    }

    @Override
    public String getPluginVersion() {
        return null;
    }

    @Override
    public String getPluginBootstrapClass() {
        return null;
    }

    @Override
    public String getPluginPath() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getRequires() {
        return null;
    }

    @Override
    public String getProvider() {
        return null;
    }

    @Override
    public String getLicense() {
        return null;
    }

    @Override
    public List<PluginDependency> getPluginDependency() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Manifest getManifest() {
        return null;
    }

    @Override
    public String getConfigFileName() {
        return null;
    }

    @Override
    public Path getInsidePluginPath() {
        return null;
    }

    @Override
    public String getPluginFileName() {
        return null;
    }

    @Override
    public String getPluginClassPath() {
        return null;
    }

    @Override
    public Set<String> getPluginLibPaths() {
        return null;
    }

    @Override
    public PluginDescriptor toPluginDescriptor() {
        return null;
    }
}
