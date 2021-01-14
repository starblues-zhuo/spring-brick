package com.gitee.starblues.integration.pf4j;

import org.pf4j.*;
import org.pf4j.util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 生成环境下jar包的加载器
 * @author starBlues
 * @version 2.4.0
 */
public class JarPluginLoader implements PluginLoader {

    protected PluginManager pluginManager;

    public JarPluginLoader(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public boolean isApplicable(Path pluginPath) {
        return Files.exists(pluginPath) && FileUtils.isJarFile(pluginPath);
    }

    @Override
    public ClassLoader loadPlugin(Path pluginPath, PluginDescriptor pluginDescriptor) {
        PluginClassLoader pluginClassLoader =
                new PluginClassLoader(pluginManager, pluginDescriptor, getClass().getClassLoader(),
                        ClassLoadingStrategy.APD);
        pluginClassLoader.addFile(pluginPath.toFile());

        return pluginClassLoader;
    }

}
