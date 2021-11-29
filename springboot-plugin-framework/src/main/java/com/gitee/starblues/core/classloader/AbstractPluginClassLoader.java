package com.gitee.starblues.core.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 插件 ClassLoader
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractPluginClassLoader extends URLClassLoader {
    public AbstractPluginClassLoader() {
        super(new URL[]{}, null);
    }
}
