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

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;

/**
 * 插件SpringApplication实现
 * @author starBlues
 * @version 3.0.0
 */
public class PluginSpringApplication extends SpringApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProcessorContext.RunMode runMode;

    private final SpringPluginProcessor pluginProcessor;
    private final ProcessorContext processorContext;

    private final GenericApplicationContext applicationContext;

    private final DefaultListableBeanFactory beanFactory;
    private final ResourceLoader resourceLoader;
    private final ConfigurePluginEnvironment configurePluginEnvironment;

    public PluginSpringApplication(SpringPluginProcessor pluginProcessor,
                                   ProcessorContext processorContext,
                                   Class<?>... primarySources) {
        super(primarySources);
        this.runMode = processorContext.runMode();
        this.pluginProcessor = pluginProcessor;
        this.processorContext = processorContext;
        this.resourceLoader = processorContext.getResourceLoader();
        this.beanFactory = new PluginListableBeanFactory(processorContext.getMainApplicationContext());
        this.configurePluginEnvironment = new ConfigurePluginEnvironment(processorContext);
        this.applicationContext = getApplicationContext();
        setDefaultPluginConfig();
    }

    protected GenericApplicationContext getApplicationContext(){
        if(runMode == ProcessorContext.RunMode.ONESELF){
            return (GenericApplicationContext) super.createApplicationContext();
        } else {
            return new PluginApplicationContext(beanFactory, processorContext);
        }
    }

    public void setDefaultPluginConfig(){
        if(runMode == ProcessorContext.RunMode.PLUGIN){
            setResourceLoader(resourceLoader);
            setBannerMode(Banner.Mode.OFF);
            setEnvironment(new StandardEnvironment());
            setWebApplicationType(WebApplicationType.NONE);
            setRegisterShutdownHook(false);
            setLogStartupInfo(false);
        }
    }

    @Override
    protected void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        super.configureEnvironment(environment, args);
        configurePluginEnvironment.configureEnvironment(environment, args);
    }

    @Override
    protected ConfigurableApplicationContext createApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public ConfigurableApplicationContext run(String... args) {
        try {
            processorContext.setApplicationContext(this.applicationContext);
            pluginProcessor.initialize(processorContext);
            return super.run(args);
        } catch (Exception e) {
            pluginProcessor.failure(processorContext);
            logger.error("启动插件[{}]失败. {}",
                    processorContext.getPluginDescriptor().getPluginId(),
                    e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void refresh(ConfigurableApplicationContext applicationContext) {
        pluginProcessor.refreshBefore(processorContext);
        super.refresh(applicationContext);
        pluginProcessor.refreshAfter(processorContext);
    }

}
