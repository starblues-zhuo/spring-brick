package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.classloader.GenericClassLoader;
import com.gitee.starblues.utils.ObjectUtils;

import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

/**
 * 主程序启动者
 * @author starBlues
 * @version 3.0.0
 */
public class MainProgramLauncher extends AbstractLauncher<ClassLoader>{

    private static final String MAIN_CLASS_LOADER_NAME = "MainProgramLauncherClassLoader";

    private final SpringBootstrap springBootstrap;

    public MainProgramLauncher(SpringBootstrap springBootstrap) {
        this.springBootstrap = springBootstrap;
    }

    @Override
    protected ClassLoader createClassLoader() throws Exception {
        GenericClassLoader classLoader = new GenericClassLoader(MAIN_CLASS_LOADER_NAME, getParentClassLoader());
        addResource(classLoader);
        return classLoader;
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        MethodRunner run = new MethodRunner(springBootstrap.getClass().getName(), "run", args);
        run.run(classLoader);
        return classLoader;
    }

    protected ClassLoader getParentClassLoader(){
        return MainProgramLauncher.class.getClassLoader();
    }

    protected void addResource(GenericClassLoader classLoader) throws Exception{
        String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        if(!ObjectUtils.isEmpty(classPath)){
            String[] classPathStr = classPath.split(";");
            for (String path : classPathStr) {
                classLoader.addResource(path);
            }
        }
        ClassLoader sourceClassLoader = Thread.currentThread().getContextClassLoader();
        if(sourceClassLoader instanceof URLClassLoader){
            URLClassLoader urlClassLoader = (URLClassLoader) sourceClassLoader;
            final URL[] urLs = urlClassLoader.getURLs();
            for (URL url : urLs) {
                classLoader.addResource(url);
            }
        }
    }


}
