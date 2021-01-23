package com.gitee.starblues.factory.process.pipe.loader;

import com.gitee.starblues.factory.PluginRegistryInfo;
import org.pf4j.ClassLoadingStrategy;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 * 插件资源实现类.主要是对Spring中的抽象的Resource实现.
 * 功能: 主要是获取插件包中的文件资源。
 * @author starBlues
 * @version 2.2.1
 */
public class PluginResource implements Resource {

    private final static Logger log = LoggerFactory.getLogger(PluginResource.class);

    private final ClassLoader classLoader;
    private final PluginWrapper pluginWrapper;

    private final long lastModified;
    private final String path;


    /**
     * 相对Classpath 路径
     * @param path 路径
     * @param pluginRegistryInfo pluginRegistryInfo bean
     */
    public PluginResource(String path, PluginRegistryInfo pluginRegistryInfo) {
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;

        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        this.classLoader = pluginRegistryInfo.getDefaultPluginClassLoader();
        this.pluginWrapper = pluginWrapper;

        this.lastModified = pluginRegistryInfo.getBasePlugin().getBasePluginExtend().getStartTimestamp();
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return classLoader.getResourceAsStream(path);
    }


    @Override
    public long contentLength() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isFileURL(url)) {
            return getFile().length();
        }
        if(ResourceUtils.isJarURL(url)){
            URLConnection con = getURL().openConnection();
            return con.getContentLength();
        }
        return 0L;
    }

    @Override
    public long lastModified() throws IOException {
        return lastModified;
    }


    @Override
    public Resource createRelative(String relativePath) {
        throw new RuntimeException("This method is not supported");
    }

    @Override
    public String getFilename() {
        return StringUtils.getFilename(this.path);
    }

    @Override
    public String getDescription() {
        return pluginWrapper.getDescriptor().getPluginDescription();
    }

    @Override
    public boolean exists() {
        try {
            URL url = getURL();
            if(url == null){
                return false;
            }
            if (ResourceUtils.isFileURL(url)) {
                return getFile().exists();
            }
            if (contentLength() >= 0) {
                return true;
            }
            InputStream is = getInputStream();
            is.close();
            return true;
        } catch (Exception e){
            log.debug(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        try {
            URL url = getURL();
            if (ResourceUtils.isFileURL(url)) {
                File file = getFile();
                return (file.canRead() && !file.isDirectory());
            } else {
                return true;
            }
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isOpen() {
        return false;
    }



    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        if (ResourceUtils.isJarURL(url)) {
            URL actualUrl = ResourceUtils.extractArchiveURL(url);
            return ResourceUtils.getFile(actualUrl, "Jar URL");
        } else {
            return ResourceUtils.getFile(url, getDescription());
        }
    }

    @Override
    public URL getURL() throws IOException {
        return classLoader.getResource(path);
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException ex) {
            throw new NestedIOException("Invalid URI [" + url + "]", ex);
        }
    }

}
