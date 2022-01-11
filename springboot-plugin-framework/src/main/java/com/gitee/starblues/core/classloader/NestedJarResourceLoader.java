package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * @author starBlues
 * @version 1.0
 */
public class NestedJarResourceLoader extends AbstractResourceLoader{

    private final PluginDescriptor pluginDescriptor;
    private final ResourceLoaderFactory resourceLoaderFactory;

    public NestedJarResourceLoader(PluginDescriptor pluginDescriptor, ResourceLoaderFactory resourceLoaderFactory) throws Exception {
        super(new URL("jar:" + pluginDescriptor.getPluginPath().toUri().toURL() + "!/"));
        this.pluginDescriptor = pluginDescriptor;
        this.resourceLoaderFactory = resourceLoaderFactory;
    }

    @Override
    public void init() throws Exception {
        super.init();
        try (JarFile jarFile = new JarFile(pluginDescriptor.getPluginPath().toFile())) {
            addClassPath(jarFile);
            addLib(jarFile);
        }
    }

    private void addClassPath(JarFile jarFile) throws Exception{
        String classesPath = pluginDescriptor.getPluginClassPath();
        Enumeration<JarEntry> entries = jarFile.entries();
        JarEntry jarEntry;
        while (entries.hasMoreElements()){
            jarEntry = entries.nextElement();
            if(!jarEntry.getName().startsWith(classesPath)){
                continue;
            }
            String realName = jarEntry.getName().replace(classesPath, "");
            URL url = new URL(baseUrl.toString() + jarEntry.getName());
            Resource resource = new Resource(realName, baseUrl, url);
            addResource(realName, resource);
        }
    }

    private void addLib(JarFile jarFile) throws Exception {
        JarEntry jarEntry = null;
        Set<String> pluginLibPaths = pluginDescriptor.getPluginLibPaths();
        for (String pluginLibPath : pluginLibPaths) {
            jarEntry = jarFile.getJarEntry(pluginLibPath);
            InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
            URL url = new URL(baseUrl.toString() + pluginLibPath + "!/");
            JarResourceLoader jarResourceLoader = new JarResourceLoader(url, new JarInputStream(jarFileInputStream));
            jarResourceLoader.init();
            resourceLoaderFactory.addResourceLoader(jarResourceLoader);
        }
    }

    @Override
    public void clear() {
        super.clear();
    }
}
