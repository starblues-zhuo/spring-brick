package com.gitee.starblues.core.classloader;

import java.net.URL;
import java.nio.file.Paths;

/**
 * 资源信息
 * @author starBlues
 * @version 3.0.0
 */
public class Resource {

    private final String name;
    private final URL baseUrl;
    private final URL url;

    public Resource(String name, URL baseUrl, URL url) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public URL getUrl() {
        return url;
    }

    void tryCloseUrlSystemFile(){
        try {
            Paths.get(baseUrl.toURI()).getFileSystem().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Paths.get(url.toURI()).getFileSystem().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
