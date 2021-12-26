package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.launcher.archive.Archive;

import java.util.Iterator;

/**
 * @author starBlues
 * @version 1.0
 */
public class ClasspathLauncher extends AbstractLauncher<ClassLoader>{




    @Override
    protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
        return super.getClassPathArchivesIterator();
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        return classLoader;
    }
}
