package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.launcher.archive.Archive;
import com.gitee.starblues.core.launcher.jar.JarFile;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @author starBlues
 * @version 1.0
 */
public abstract class AbstractLauncher<R> implements Launcher<R> {

    @Override
    public R run(String... args) throws Exception {
        JarFile.registerUrlProtocolHandler();
        ClassLoader classLoader = createClassLoader(getClassPathArchivesIterator());
        Thread thread = Thread.currentThread();
        ClassLoader oldClassLoader = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(classLoader);
            return launch(classLoader, args);
        } finally {
            thread.setContextClassLoader(oldClassLoader);
        }
    }

    protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
        return Collections.emptyListIterator();
    }

    protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList<>(50);
        while (archives.hasNext()) {
            urls.add(archives.next().getUrl());
        }
        return createClassLoader(urls.toArray(new URL[0]));
    }

    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
    }

    /**
     * 子类实现具体的启动方法
     * @param classLoader 当前的 ClassLoader
     * @param args 启动参数
     * @return 启动返回值
     * @throws Exception 启动异常
     */
    protected abstract R launch(ClassLoader classLoader, String... args) throws Exception;

}
