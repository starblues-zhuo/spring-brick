package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源加载工厂
 * @author starBlues
 * @version 3.0.0
 */
public class ResourceLoaderFactory extends AbstractResourceLoader {

    private final List<AbstractResourceLoader> resourceLoaders = new ArrayList<>();

    public ResourceLoaderFactory() {
        super(null);
    }


    void addResourceLoader(AbstractResourceLoader resourceLoader){
        if(resourceLoader != null){
            resourceLoaders.add(resourceLoader);
        }
    }

    public void addResource(String path) throws Exception{
        if(path == null || "".equals(path)){
            return;
        }
        addResource(Paths.get(path));
    }

    public void addResource(File file) throws Exception{
        if(file == null){
            return;
        }
        addResource(file.toPath());
    }

    public void addResource(Path path) throws Exception{
        if(path == null){
            return;
        }
        if(!Files.exists(path)){
            return;
        }
        URL url = path.toUri().toURL();
        AbstractResourceLoader resourceLoader = null;
        if(ResourceUtils.isJarFile(path)) {
            resourceLoader = new JarResourceLoader(url);
        } else if(Files.isDirectory(path)){
            resourceLoader = new ClassPathLoader(url);
        }
        if(resourceLoader != null){
            resourceLoader.init();
            resourceLoaders.add(resourceLoader);
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
