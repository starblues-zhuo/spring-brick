/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.launcher.plugin.CacheRegistryInfo;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.spring.WebConfig;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/**
 * 默认的处理者上下文
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultProcessorContext extends CacheRegistryInfo implements ProcessorContext{

    private final RunMode runMode;

    private final SpringPluginBootstrap springPluginBootstrap;
    private final PluginInteractive pluginInteractive;
    private final Class<? extends SpringPluginBootstrap> runnerClass;
    private final MainApplicationContext mainApplicationContext;
    private final ClassLoader classLoader;
    private final ResourceLoader resourceLoader;

    private final IntegrationConfiguration configuration;
    private final WebConfig webConfig;

    private GenericApplicationContext applicationContext;

    public DefaultProcessorContext(RunMode runMode, SpringPluginBootstrap springPluginBootstrap,
                                   PluginInteractive pluginInteractive, Class<? extends SpringPluginBootstrap> runnerClass) {
        this.runMode = runMode;
        this.springPluginBootstrap = springPluginBootstrap;
        this.pluginInteractive = pluginInteractive;
        this.runnerClass = runnerClass;
        this.classLoader = getPluginClassLoader();
        this.resourceLoader = new DefaultResourceLoader(this.classLoader);
        this.mainApplicationContext = pluginInteractive.getMainApplicationContext();
        this.configuration = pluginInteractive.getConfiguration();
        this.webConfig = new WebConfig();
    }

    @Override
    public RunMode runMode() {
        return runMode;
    }

    @Override
    public SpringPluginBootstrap getSpringPluginBootstrap() {
        return springPluginBootstrap;
    }

    @Override
    public InsidePluginDescriptor getPluginDescriptor() {
        return pluginInteractive.getPluginDescriptor();
    }

    @Override
    public Class<? extends SpringPluginBootstrap> getRunnerClass() {
        return runnerClass;
    }

    @Override
    public PluginInteractive getPluginInteractive() {
        return pluginInteractive;
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    public SpringBeanFactory getMainBeanFactory() {
        return mainApplicationContext.getSpringBeanFactory();
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public GenericApplicationContext getApplicationContext() {
        if(applicationContext == null){
            return null;
        }
        return applicationContext;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    @Override
    public WebConfig getWebConfig() {
        return webConfig;
    }

    @Override
    public void setApplicationContext(GenericApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    protected ClassLoader getPluginClassLoader(){
        return ClassUtils.getDefaultClassLoader();
    }
}
