package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.exception.PluginProhibitStopException;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.WebConfig;
import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;
import com.gitee.starblues.utils.ResourceUtils;

/**
 * SpringPluginHook-Wrapper
 * @author starBlues
 * @version 3.0.0
 */
public class SpringPluginHookWrapper implements SpringPluginHook {

    private final SpringPluginHook target;
    private final InsidePluginDescriptor descriptor;
    private final PluginLaunchInvolved pluginLaunchInvolved;
    private final ClassLoader classLoader;

    public SpringPluginHookWrapper(SpringPluginHook target, InsidePluginDescriptor descriptor,
                                   PluginLaunchInvolved pluginLaunchInvolved,
                                   ClassLoader classLoader) {
        this.target = target;
        this.descriptor = descriptor;
        this.pluginLaunchInvolved = pluginLaunchInvolved;
        this.classLoader = classLoader;
    }

    @Override
    public void stopVerify() throws PluginProhibitStopException {
        target.stopVerify();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return target.getApplicationContext();
    }

    @Override
    public WebConfig getWebConfig() {
        return target.getWebConfig();
    }

    @Override
    public ThymeleafConfig getThymeleafConfig() {
        return null;
    }

    @Override
    public void close() throws Exception {
        pluginLaunchInvolved.close(descriptor, classLoader);
        ResourceUtils.closeQuietly(target);
        ResourceUtils.closeQuietly(classLoader);
    }
}
