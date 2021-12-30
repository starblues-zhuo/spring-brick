package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.*;
import com.gitee.starblues.core.descriptor.DevPluginDescriptorLoader;
import com.gitee.starblues.core.descriptor.EmptyPluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.launcher.plugin.DefaultPluginInteractive;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.extract.DefaultOpExtractFactory;
import com.gitee.starblues.spring.invoke.DefaultInvokeSupperCache;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件引导抽象类。插件入口需集成本抽象类
 * @author starBlues
 * @version 3.0.0
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
        SpringPluginProcessor pluginProcessor = new ComposeSpringPluginProcessor(runMode, customPluginProcessors);
        ProcessorContext processorContext = new DefaultProcessorContext(
                pluginInteractive,
                this.getClass()
        );
        PluginSpringApplication springApplication = new PluginSpringApplication(
                pluginProcessor,
                processorContext,
                primarySources);
        springApplication.run(args);
        return new DefaultSpringPluginHook(pluginProcessor, processorContext);
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

    protected void createPluginInteractive(){
        if(pluginInteractive != null){
            return;
        }
        createPluginInteractiveOfOneself();
    }

    protected void createPluginInteractiveOfOneself(){
        this.pluginInteractive = new PluginOneselfInteractive();
    }

}
