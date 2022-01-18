package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * 插件 classLoader
 * @author starBlues
 * @version 3.0.0
 */
public class PluginClassLoader extends GenericClassLoader {

    private MainResourceMatcher mainResourceMatcher;

    public PluginClassLoader(String name) {
        this(name, null);
    }

    public PluginClassLoader(String name, ClassLoader parentClassLoader) {
        this(name, parentClassLoader, null);
    }

    public PluginClassLoader(String name, ClassLoader parentClassLoader, MainResourcePatternDefiner patternDefiner) {
        super(name, parentClassLoader, new PluginResourceLoaderFactory());
        if(patternDefiner != null){
            setMainResourceMatcher(new CacheMainResourceMatcher(patternDefiner));
        } else {
            setMainResourceMatcher(new ProhibitMainResourceMatcher());
        }
    }

    public void addResource(InsidePluginDescriptor pluginDescriptor) throws Exception {
        ((PluginResourceLoaderFactory) resourceLoaderFactory).addResource(pluginDescriptor);
    }

    public void setMainResourceMatcher(MainResourceMatcher mainResourceMatcher){
        this.mainResourceMatcher = Assert.isNotNull(mainResourceMatcher, "参数 mainResourceMatcher 不能为空");
    }


    @Override
    protected Class<?> findClassFromParent(String className) throws ClassNotFoundException {
        if(mainResourceMatcher.match(className.replace(".", "/"))){
            try {
                return super.findClassFromParent(className);
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    @Override
    protected InputStream findInputStreamFromParent(String name) {
        if(mainResourceMatcher.match(name)){
            try {
                return super.findInputStreamFromParent(name);
            } catch (Exception e){
                // 忽略
            }
        }
        return null;
    }

    @Override
    protected URL findResourceFromParent(String name) {
        if(mainResourceMatcher.match(name)){
            return super.findResourceFromParent(name);
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResourcesFromParent(String name) throws IOException {
        if(mainResourceMatcher.match(name)){
            return super.findResourcesFromParent(name);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if(mainResourceMatcher instanceof AutoCloseable){
            try {
                ((AutoCloseable) mainResourceMatcher).close();
            } catch (Exception e){
                throw new IOException(e);
            }
        }
    }

}
