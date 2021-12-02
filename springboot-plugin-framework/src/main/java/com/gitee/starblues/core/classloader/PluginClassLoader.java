package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.ResourceClear;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件 classLoader
 * @author starBlues
 * @version 3.0.0
 */
public class PluginClassLoader extends AbstractPluginClassLoader implements ResourceClear {

    private final Map<String, Class<?>> pluginClassCache = new ConcurrentHashMap<>();

    private final String pluginId;
    private final ClassLoader parent;
    private final ResourceLoaderFactory resourceLoaderFactory;

    private MainResourceMatcher mainResourceMatcher;


    public PluginClassLoader(String pluginId,
                             Path classpath,
                             ClassLoader parentClassLoader,
                             MainResourcePatternDefiner mainResourcePatternDefiner) {
        resourceLoaderFactory = new ResourceLoaderFactory();
        resourceLoaderFactory.addResource(classpath);

        this.pluginId = Assert.isNotEmpty(pluginId, "参数 pluginId 不能为空");
        this.parent = Assert.isNotNull(parentClassLoader, "参数 parentClassLoader 不能为空");
        MainResourcePatternDefiner patternDefiner = Assert.isNotNull(mainResourcePatternDefiner,
                "参数 mainResourcePatternDefiner 不能为空");
        setMainResourceMatcher(new CacheMainResourceMatcher(patternDefiner));
    }

    protected void setMainResourceMatcher(MainResourceMatcher mainResourceMatcher){
        this.mainResourceMatcher = Assert.isNotNull(mainResourceMatcher, "参数 mainResourceMatcher 不能为空");
    }


    public void addResource(File file) {
        resourceLoaderFactory.addResource(file);
    }

    public String getPluginId() {
        return pluginId;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            Class<?> loadedClass = null;
            if(mainResourceMatcher.match(className.replace(".", "/"))){
                try {
                    loadedClass = parent.loadClass(className);
                } catch (Exception e){
                    // 忽略
                }
            }
            if(loadedClass == null){
                loadedClass = findLoadedClass(className);
            }
            if (loadedClass != null) {
                return loadedClass;
            }
            loadedClass = findPluginClass(className);
            if (loadedClass != null) {
                return loadedClass;
            }
            throw new ClassNotFoundException(className);
        }
    }

    private Class<?> findPluginClass(String name) {
        synchronized (pluginClassCache){
            Class<?> aClass;
            String formatClassName = formatClassName(name);
            aClass = pluginClassCache.get(formatClassName);
            if (aClass != null) {
                return aClass;
            }

            Resource resource = resourceLoaderFactory.findResource(formatClassName);
            if(resource == null){
                return null;
            }
            byte[] bytes = resource.getBytes();
            aClass = defineClass(name, bytes, 0, bytes.length );
            if(aClass == null) {
                return null;
            }
            if (aClass.getPackage() == null) {
                int lastDotIndex = name.lastIndexOf( '.' );
                String packageName = (lastDotIndex >= 0) ? name.substring( 0, lastDotIndex) : "";
                definePackage(packageName, null, null, null,
                        null, null, null, null );
            }
            pluginClassCache.put(name, aClass);
            return aClass;
        }
    }

    @Override
    public URL[] getURLs() {
        List<Resource> resources = resourceLoaderFactory.getResources();
        URL[] urls = new URL[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            urls[i] = resources.get(i).getUrl();
        }
        return urls;
    }


    @Override
    public InputStream getResourceAsStream(String name) {
        name = formatResourceName(name);
        InputStream inputStream = null;
        if(mainResourceMatcher.match(name)){
            try {
                inputStream = parent.getResourceAsStream(name);
            } catch (Exception e){
                // 忽略
            }
        }
        if(inputStream != null){
            return inputStream;
        }
        return resourceLoaderFactory.getInputStream(name);
    }

    @Override
    public URL getResource(String name) {
        name = formatResourceName(name);
        URL url = null;
        if(mainResourceMatcher.match(name)){
            url = parent.getResource(name);
        }
        if(url != null){
            return url;
        }
        Resource resource = resourceLoaderFactory.findResource(name);
        if(resource == null){
            return null;
        }
        return resource.getUrl();
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        name = formatResourceName(name);
        Vector<URL> vector = new Vector<>();
        if(mainResourceMatcher.match(name)){
            Enumeration<URL> enumeration = parent.getResources(name);
            while (enumeration.hasMoreElements()){
                URL url = enumeration.nextElement();
                try {
                    URI uri = url.toURI();
                    String path = Paths.get(uri).toString();
                    if(mainResourceMatcher.matchSpringFactories(path)){
                        vector.add(url);
                    }
                } catch (Exception e) {
                    if(mainResourceMatcher.matchSpringFactories(url.getPath())){
                        vector.add(url);
                    }
                }
            }
        }
        List<Resource> resourceList = resourceLoaderFactory.findResources(name);
        if(!ObjectUtils.isEmpty(resourceList)){
            for (Resource resource : resourceList) {
                vector.add(resource.getUrl());
            }
        }
        return vector.elements();
    }

    private String formatClassName(String className) {
        className = className.replace( '/', '~' );
        className = className.replace( '.', '/' ) + ".class";
        className = className.replace( '~', '/' );
        return className;
    }

    private String formatResourceName(String resourceName){
        if(ObjectUtils.isEmpty(resourceName)) {
            return resourceName;
        }
        String[] split = resourceName.split("/");
        StringBuilder newPath = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if("".equals(s)){
                continue;
            }
            if(i == 0){
                newPath = new StringBuilder(s);
            } else {
                newPath.append(ResourceUtils.PACKAGE_SPLIT).append(s);
            }
        }
        return newPath.toString();
    }

    @Override
    public void clear(){
        synchronized (pluginClassCache){
            pluginClassCache.clear();
            resourceLoaderFactory.clear();
            if(mainResourceMatcher instanceof ResourceClear){
                ((ResourceClear) mainResourceMatcher).clear();
            }
        }
    }


}
