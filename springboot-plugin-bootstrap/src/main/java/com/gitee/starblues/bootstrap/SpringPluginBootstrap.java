package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.DefaultProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessorFactory;
import com.gitee.starblues.core.descriptor.DevPluginDescriptorLoader;
import com.gitee.starblues.core.descriptor.EmptyPluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.launcher.plugin.DefaultPluginInteractive;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.processor.invoke.DefaultInvokeSupperCache;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public abstract class SpringPluginBootstrap {

    private SpringPluginProcessor.RunMode runMode = SpringPluginProcessor.RunMode.ONESELF;

    private volatile PluginInteractive pluginInteractive;

    private final List<SpringPluginProcessor> customPluginProcessors = new ArrayList<>();

    public SpringPluginHook run(String[] args){
        return run(this.getClass(), args);
    }

    public SpringPluginHook run(Class<?> primarySources, String[] args){
        return run(new Class[]{ primarySources }, args);
    }

    public SpringPluginHook run(Class<?>[] primarySources, String[] args){
        return start(primarySources, args);
    }

    private SpringPluginHook start(Class<?>[] primarySources, String[] args){
        createPluginInteractive();
        SpringPluginProcessor springPluginProcessor = new SpringPluginProcessorFactory(runMode, customPluginProcessors);
        ProcessorContext processorContext = new DefaultProcessorContext(
                pluginInteractive,
                this.getClass()
        );
        PluginSpringApplication springApplication = new PluginSpringApplication(
                springPluginProcessor,
                processorContext,
                primarySources);
        springApplication.run(args);
        return new DefaultSpringPluginHook(springPluginProcessor, processorContext);
    }

    public final void setPluginInteractive(PluginInteractive pluginInteractive) {
        this.pluginInteractive = pluginInteractive;
        this.runMode = SpringPluginProcessor.RunMode.PLUGIN;
    }

    public final void addSpringPluginProcessor(SpringPluginProcessor springPluginProcessor){
        if(springPluginProcessor != null){
            customPluginProcessors.add(springPluginProcessor);
        }
    }

    private void createPluginInteractive(){
        if(pluginInteractive != null){
            return;
        }
        PluginDescriptor pluginDescriptor = createPluginDescriptor();
        MainApplicationContext mainApplicationContext = new EmptyMainApplicationContext();
        pluginInteractive = new DefaultPluginInteractive(pluginDescriptor, mainApplicationContext,
                new AutoIntegrationConfiguration(),
                new DefaultInvokeSupperCache());
    }

    private PluginDescriptor createPluginDescriptor(){
        PluginDescriptor pluginDescriptor;
        try {
            PluginDescriptorLoader pluginDescriptorLoader = new DevPluginDescriptorLoader();
            pluginDescriptor = pluginDescriptorLoader.load(
                    Paths.get(this.getClass().getResource("/").toURI()));
            if(pluginDescriptor == null){
                pluginDescriptor = new EmptyPluginDescriptor();
            }
        } catch (Exception e){
            pluginDescriptor = new EmptyPluginDescriptor();
        }
        return pluginDescriptor;
    }

}
