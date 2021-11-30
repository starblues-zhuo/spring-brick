package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.PluginFileUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 */
public class ResourceLoaderFactory extends AbstractResourceLoader {

    private final List<AbstractResourceLoader> resourceLoaders = new ArrayList<>();

    public ResourceLoaderFactory() {
        super(null);
    }


    public void addResource(String path) {
        if(path == null || "".equals(path)){
            return;
        }
        addResource(Paths.get(path));
    }

    public void addResource(File file) {
        if(file == null){
            return;
        }
        addResource(file.toPath());
    }

    public synchronized void addResource(Path path) {
        if(path == null){
            return;
        }
        try {
            URL url = path.toUri().toURL();
            AbstractResourceLoader resourceLoader = null;
            if(PluginFileUtils.isJarFile(path)) {
                resourceLoader = new JarResourceLoader(url);
            } else if(Files.isDirectory(path)){
                resourceLoader = new ClassPathLoader(url);
            }
            if(resourceLoader != null){
                resourceLoader.init();
                resourceLoaders.add(resourceLoader);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Resource> getResources() {
        List<Resource> resources = new ArrayList<>();
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            resources.addAll(resourceLoader.getResources());
        }
        return resources;
    }

    @Override
    public synchronized Resource findResource(String name) {
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            Resource resourceInfo = resourceLoader.findResource(name);
            if(resourceInfo != null){
                return resourceInfo;
            }
        }
        return null;
    }

    public synchronized List<Resource> findResources(String name) {
        List<Resource> resourceInfos = new ArrayList<>();
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            Resource resource = resourceLoader.findResource(name);
            if(resource != null){
                resourceInfos.add(resource);
            }
        }
        return resourceInfos;
    }

    @Override
    public synchronized InputStream getInputStream(String name) {
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            InputStream inputStream = resourceLoader.getInputStream(name);
            if(inputStream != null){
                return inputStream;
            }
        }
        return null;
    }

    @Override
    public synchronized void clear() {
        for (AbstractResourceLoader resourceLoader : resourceLoaders) {
            try {
                resourceLoader.clear();
            } catch (Exception e){
                // 忽略
            }
        }
        resourceLoaders.clear();
    }
}
