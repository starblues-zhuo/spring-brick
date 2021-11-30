package com.gitee.starblues.core.descriptor;

import java.nio.file.Path;
import java.util.List;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyPluginDescriptor implements PluginDescriptor{
    @Override
    public String getPluginId() {
        return null;
    }

    @Override
    public String getPluginVersion() {
        return null;
    }

    @Override
    public String getPluginClass() {
        return null;
    }

    @Override
    public Path getPluginPath() {
        return null;
    }

    @Override
    public String getPluginLibDir() {
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
    public String getConfigFileName() {
        return null;
    }

    @Override
    public List<PluginDependency> getPluginDependency() {
        return null;
    }
}
