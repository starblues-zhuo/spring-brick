package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.Assert;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * jar 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class JarResourceLoader extends AbstractResourceLoader{

    private final URL sourceUrl;

    public JarResourceLoader(URL url)  throws Exception{
        super(new URL("jar:" + url.toString() + "!/"));
        this.sourceUrl = Assert.isNotNull(url, "url 不能为空");
    }

    @Override
    public void init() throws Exception {
        // 解析
        try (InputStream fileInputStream = sourceUrl.openStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             JarInputStream jarStream = new JarInputStream(bufferedInputStream);
        ){
            JarEntry jarEntry = null;
            while ((jarEntry = jarStream.getNextJarEntry()) != null) {
                String name = jarEntry.getName();
                URL url = new URL(baseUrl.toString() + name);
                if (jarEntry.isDirectory()) {
                    Resource resource = new Resource(
                            name, baseUrl, url, null
                    );
                    addResource(name, resource);
                    continue;
                }
                if (existResource(name)) {
                    continue;
                }
                byte[] bytes = new byte[2048];

                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    int len = 0;
                    while ((len = jarStream.read(bytes)) > 0) {
                        out.write(bytes, 0, len);
                    }
                    Resource resource = new Resource(
                            name, baseUrl, url, out.toByteArray()
                    );
                    addResource(name, resource);
                }
            }
        }
    }
}
