package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.launcher.PluginResourceStorage;
import com.gitee.starblues.core.launcher.jar.Handler;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;

/**
 * @author starBlues
 * @version 1.0
 */
public class SpringPluginHookWrapper implements SpringPluginHook {

    private final SpringPluginHook target;
    private final InsidePluginDescriptor descriptor;
    private final ClassLoader classLoader;

    public SpringPluginHookWrapper(SpringPluginHook target,
                                   InsidePluginDescriptor descriptor,
                                   ClassLoader classLoader) {
        this.target = target;
        this.descriptor = descriptor;
        this.classLoader = classLoader;
    }


    @Override
    public ApplicationContext getApplicationContext() {
        return target.getApplicationContext();
    }

    @Override
    public void close() throws Exception {
        if(target != null){
            target.close();
        }
        if(classLoader instanceof AutoCloseable){
            ((AutoCloseable)classLoader).close();
        }
        PluginResourceStorage.removePlugin(descriptor.getPluginId());
    }
}
