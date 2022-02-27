package com.gitee.starblues.loader.classloader.resource.loader;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

/**
 * 资源加载工厂
 *
 * @author starBlues
 * @version 3.0.0
 */
public interface ResourceLoaderFactory extends AutoCloseable{


    void addResource(String path) throws Exception;

    void addResource(File file) throws Exception;

    void addResource(Path path) throws Exception;

    void addResource(URL url) throws Exception;

    void addResource(ResourceLoader resourceLoader) throws Exception;

    Resource findResource(String name);

    List<Resource> findResources(String name);

    InputStream getInputStream(String name);

    List<Resource> getResources();

}
