package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;
import com.gitee.starblues.core.classloader.PluginClassLoader;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.launcher.AbstractLauncher;
import com.gitee.starblues.core.launcher.JavaMainResourcePatternDefiner;
import com.gitee.starblues.core.launcher.MainProgramLauncher;
import com.gitee.starblues.core.launcher.PluginResourceStorage;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 插件启动引导类
 * @author starBlues
 * @version 3.0.0
 */
public class PluginLauncher extends AbstractLauncher<SpringPluginHook> {

    protected final PluginInteractive pluginInteractive;
    protected final InsidePluginDescriptor pluginDescriptor;

    public PluginLauncher(PluginInteractive pluginInteractive) {
        this.pluginInteractive = pluginInteractive;
        this.pluginDescriptor = pluginInteractive.getPluginDescriptor();
    }

    @Override
    protected ClassLoader createClassLoader() throws Exception {
        String pluginId = pluginDescriptor.getPluginId();
        MainResourcePatternDefiner mainResourceMatcher = new JavaMainResourcePatternDefiner() {
            @Override
            public Set<String> getIncludePatterns() {
                Set<String> includeResourcePatterns = super.getIncludePatterns();
                includeResourcePatterns.add("com/gitee/starblues/**");
                includeResourcePatterns.add("org/springframework/web/**");
                return includeResourcePatterns;
            }
        };
        PluginClassLoader pluginClassLoader = new PluginClassLoader(
                pluginId, MainProgramLauncher.class.getClassLoader(), mainResourceMatcher
        );
        pluginClassLoader.addResource(pluginDescriptor);
        //TODO 添加框架的引导
        pluginClassLoader.addResource(Paths.get("D:\\etc\\kitte\\ksm\\springboot-plugin-framework-parent\\springboot-plugin-bootstrap\\target\\classes"));
        return pluginClassLoader;
    }

    @Override
    protected SpringPluginHook launch(ClassLoader classLoader, String... args) throws Exception {
        PluginResourceStorage.addPlugin(pluginDescriptor);
        SpringPluginHook springPluginHook = (SpringPluginHook) new PluginMethodRunner(pluginInteractive).run(classLoader);
        return new SpringPluginHookWrapper(springPluginHook, pluginDescriptor, classLoader);
    }


}
