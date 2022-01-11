package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.classloader.GenericClassLoader;
import com.gitee.starblues.core.classloader.PluginClassLoader;
import com.gitee.starblues.core.launcher.archive.Archive;
import com.gitee.starblues.core.launcher.archive.ExplodedArchive;
import com.gitee.starblues.core.launcher.archive.JarFileArchive;
import com.gitee.starblues.utils.PluginFileUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class MainProgramLauncher extends AbstractLauncher<ClassLoader>{


    private final SpringBootstrap springBootstrap;

    public MainProgramLauncher(SpringBootstrap springBootstrap) {
        this.springBootstrap = springBootstrap;
    }

    @Override
    protected ClassLoader createClassLoader() throws Exception {
        GenericClassLoader classLoader = new GenericClassLoader(
                "MainProgramLauncherClassLoader", MainProgramLauncher.class.getClassLoader()
        );
        addResource(classLoader);
        return classLoader;
    }

    protected void addResource(GenericClassLoader classLoader) throws Exception{
        String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        String[] split = classPath.split(";");
        List<Archive> archives = new ArrayList<>();
        for (String s : split) {
            classLoader.addResource(s);
        }
    }


    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        MethodRunner run = new MethodRunner(springBootstrap.getClass().getName(), "run", args);
        run.run(classLoader);
        return classLoader;
    }
}
