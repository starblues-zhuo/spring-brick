package com.gitee.starblues.core.launcher;

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
    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        PluginClassLoader pluginClassLoader = new PluginClassLoader(
                "main", null, MainProgramLauncher.class.getClassLoader(),
                new JavaMainResourcePatternDefiner()
        );
        for (URL url : urls) {
            pluginClassLoader.addResource(Paths.get(url.toURI()));
        }

        return pluginClassLoader;
    }

    @Override
    protected Iterator<Archive> getClassPathArchivesIterator() throws Exception {
        // TODO 生产环境下待定
        String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        String[] split = classPath.split(";");
        List<Archive> archives = new ArrayList<>();
        for (String s : split) {
            Path path = Paths.get(s);
            File file = path.toFile();
            if(PluginFileUtils.isJarFile(path)){
                archives.add(new JarFileArchive(file));
            } else {
                archives.add(new ExplodedArchive(file));
            }
        }
        return archives.iterator();
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        MethodRunner run = new MethodRunner(springBootstrap.getClass().getName(), "run", args);
        run.run(classLoader);
        return classLoader;
    }
}
