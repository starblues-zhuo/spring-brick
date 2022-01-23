package com.gitee.starblues.core.launcher;


import com.gitee.starblues.core.launcher.jar.JarFile;

/**
 * 抽象的启动引导者
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractLauncher<R> implements Launcher<R> {


    @Override
    public R run(String... args) throws Exception {
        JarFile.registerUrlProtocolHandler();
        ClassLoader classLoader = createClassLoader();
        Thread thread = Thread.currentThread();
        ClassLoader oldClassLoader = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(classLoader);
            return launch(classLoader, args);
        } finally {
            thread.setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * 创建classloader
     * @return ClassLoader
     * @throws Exception 创建异常
     */
    protected abstract ClassLoader createClassLoader() throws Exception;

    /**
     * 子类实现具体的启动方法
     * @param classLoader 当前的 ClassLoader
     * @param args 启动参数
     * @return 启动返回值
     * @throws Exception 启动异常
     */
    protected abstract R launch(ClassLoader classLoader, String... args) throws Exception;

}
