package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件 classLoader
 * @author starBlues
 * @version 3.0.0
 */
public class PluginClassLoader extends AbstractPluginClassLoader {

    private final Map<String, Class<?>> pluginClassCache = new ConcurrentHashMap<>();

    private final String pluginId;
    private final ClassLoader parent;
    private final ResourceLoaderFactory resourceLoaderFactory;
    private final MainResourceDefiner mainResourceDefiner;

    public PluginClassLoader(String pluginId, Path classpath, ClassLoader parent) {
        this(pluginId, classpath, parent, null);
    }

    public PluginClassLoader(String pluginId, Path classpath, ClassLoader parent, MainResourceDefiner definer) {
        resourceLoaderFactory = new ResourceLoaderFactory();
        resourceLoaderFactory.addResource(classpath);
        this.pluginId = pluginId;
        this.parent = parent;
        if(definer == null){
            definer = new EmptyMainResourceDefiner();
        }
        this.mainResourceDefiner = definer;
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
            Set<String> classNames = mainResourceDefiner.getClassNames();
            Class<?> loadedClass = null;
            if(exist(classNames, className)){
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

    @Override
    public URL[] getURLs() {
        List<Resource> resources = resourceLoaderFactory.getResources();
        URL[] urls = new URL[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            urls[i] = resources.get(i).getUrl();
        }
        return urls;
    }

    private Class<?> findPluginClass(String name) {
        synchronized (pluginClassCache){
            Class<?> aClass = null;
            String formatClassName = formatClassName(name);
            aClass = pluginClassCache.get(formatClassName);
            if (aClass != null) {
                return aClass;
            }

            Resource resource = resourceLoaderFactory.findResource(formatClassName);
            if(resource == null){
                return null;
            }
            byte[] bytes = null;
            bytes = resource.getBytes();
            aClass = defineClass(name, bytes, 0, bytes.length );
            if(aClass == null) {
                return null;
            }
            if (aClass.getPackage() == null) {
                int lastDotIndex = name.lastIndexOf( '.' );
                String packageName = (lastDotIndex >= 0) ? name.substring( 0, lastDotIndex) : "";
                definePackage(packageName, null, null, null, null, null, null, null );
            }
            pluginClassCache.put(name, aClass);
            return aClass;
        }
    }


    @Override
    public InputStream getResourceAsStream(String name) {
        Set<String> resources = mainResourceDefiner.getResources();
        if(exist(resources, name)){
            return parent.getResourceAsStream(name);
        }
        return resourceLoaderFactory.getInputStream(name);
    }

    @Override
    public URL getResource(String name) {
        Set<String> resources = mainResourceDefiner.getResources();
        if(exist(resources, name)){
            return parent.getResource(name);
        }
        Resource resource = resourceLoaderFactory.findResource(name);
        if(resource == null){
            return null;
        }
        return resource.getUrl();
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Vector<URL> vector = new Vector<>();
        Set<String> resources = mainResourceDefiner.getResources();
        Set<String> springFactories = mainResourceDefiner.getSpringFactories();
        if(exist(resources, name)){
            Enumeration<URL> enumeration = parent.getResources(name);
            while (enumeration.hasMoreElements()){
                URL url = enumeration.nextElement();
                String path = url.getPath();
                if(exist(springFactories, path)){
                    vector.add(url);
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

    private boolean exist(Set<String> set, String name){
        if(ObjectUtils.isEmpty(set) || ObjectUtils.isEmpty(name)){
            return false;
        }
        for (String value : set) {
            if(name.contains(value)){
                return true;
            }
        }
        return false;
    }

    public void clear(){
        synchronized (pluginClassCache){
            pluginClassCache.clear();
            resourceLoaderFactory.clear();
        }
    }


}
