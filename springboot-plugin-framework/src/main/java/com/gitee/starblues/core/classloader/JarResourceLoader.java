package com.gitee.starblues.core.classloader;

import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * jar 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class JarResourceLoader extends AbstractResourceLoader{

    private final JarInputStream jarInputStream;

    public JarResourceLoader(URL url)  throws Exception{
        super(new URL("jar:" + url.toString() + "!/"));
        this.jarInputStream = new JarInputStream(url.openStream());
    }

    public JarResourceLoader(URL url, JarInputStream jarInputStream)  throws Exception{
        super(url);
        this.jarInputStream = jarInputStream;
    }

    @Override
    public void init() throws Exception {
        super.init();
        // 解析
        try {
            JarEntry jarEntry = null;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                String name = jarEntry.getName();
                URL url = new URL(baseUrl.toString() + name);
                Resource resource = new Resource(name, baseUrl, url);
                addResource(name, resource);
            }
        } finally {
            IOUtils.closeQuietly(jarInputStream);
        }
    }


}
