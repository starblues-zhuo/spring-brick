package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;

/**
 * @author starBlues
 * @version 1.0
 */
public class SpringPluginHookWrapper implements SpringPluginHook {

    private final SpringPluginHook target;
    private final ClassLoader classLoader;

    public SpringPluginHookWrapper(SpringPluginHook target,
                                   ClassLoader classLoader) {
        this.target = target;
        this.classLoader = classLoader;
    }


    @Override
    public ApplicationContext getApplicationContext() {
        return target.getApplicationContext();
    }

    @Override
    public void close() throws Exception {
        target.close();
        if(classLoader instanceof AutoCloseable){
            ((AutoCloseable)classLoader).close();
        }
    }
}
