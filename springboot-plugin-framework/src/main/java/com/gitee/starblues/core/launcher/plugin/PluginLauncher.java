package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.ResourceClear;
import com.gitee.starblues.core.classloader.PluginClassLoader;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.launcher.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginLauncher extends AbstractLauncher<Object> implements ResourceClear {

    private final PluginInteractive pluginInteractive;
    private final PluginDescriptor pluginDescriptor;

    public PluginLauncher(PluginInteractive pluginInteractive) {
        this.pluginInteractive = pluginInteractive;
        this.pluginDescriptor = pluginInteractive.getPluginDescriptor();
    }

    @Override
    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        String pluginLibDir = pluginDescriptor.getPluginLibDir();
        Path pluginPath = pluginDescriptor.getPluginPath();
        PluginClassLoader pluginClassLoader = new PluginClassLoader(
                "plugin", pluginPath, MainProgramLauncher.class.getClassLoader(),
                new JavaMainResourcePatternDefiner(){
                    @Override
                    public Set<String> getIncludeResourcePatterns() {
                        Set<String> includeResourcePatterns = super.getIncludeResourcePatterns();
                        includeResourcePatterns.add("com/gitee/starblues/**");
                        includeResourcePatterns.add("org/springframework/web/**");
                        return includeResourcePatterns;
                    }
                }
        );
        File file = new File(pluginLibDir);
        if(file.exists() && file.isDirectory()){
            File[] listFiles = file.listFiles();
            if(listFiles != null){
                for (File listFile : listFiles) {
                    pluginClassLoader.addResource(listFile);
                }
            }

        }
        pluginClassLoader.addResource(Paths.get("D:\\etc\\kitte\\ksm\\springboot-plugin-framework-parent\\springboot-plugin-bootstrap\\target\\classes"));

        return pluginClassLoader;
    }


    @Override
    protected Object launch(ClassLoader classLoader, String... args) throws Exception {
        return new PluginMethodRunner(pluginInteractive).run(classLoader);
    }

    @Override
    public void clear() {

    }
}
