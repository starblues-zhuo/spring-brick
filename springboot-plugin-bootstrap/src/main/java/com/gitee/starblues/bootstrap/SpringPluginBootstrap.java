package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ComposeSpringPluginProcessor;
import com.gitee.starblues.bootstrap.processor.DefaultProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.spring.SpringPluginHook;

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

    public final SpringPluginHook run(String[] args){
        return run(this.getClass(), args);
    }

    public final SpringPluginHook run(Class<?> primarySources, String[] args){
        return run(new Class[]{ primarySources }, args);
    }

    public final SpringPluginHook run(Class<?>[] primarySources, String[] args){
        return start(primarySources, args);
    }

    private SpringPluginHook start(Class<?>[] primarySources, String[] args){
        addCustomSpringPluginProcessor();
        createPluginInteractive();
        SpringPluginProcessor pluginProcessor = new ComposeSpringPluginProcessor(runMode, customPluginProcessors);
        ProcessorContext processorContext = new DefaultProcessorContext(
                this, pluginInteractive, this.getClass()
        );
        PluginSpringApplication springApplication = new PluginSpringApplication(
                pluginProcessor,
                processorContext,
                primarySources);
        springApplication.run(args);
        return new DefaultSpringPluginHook(pluginProcessor, processorContext);
    }

    public final SpringPluginBootstrap setPluginInteractive(PluginInteractive pluginInteractive) {
        this.pluginInteractive = pluginInteractive;
        this.runMode = SpringPluginProcessor.RunMode.PLUGIN;
        return this;
    }

    public final SpringPluginBootstrap addSpringPluginProcessor(SpringPluginProcessor springPluginProcessor){
        if(springPluginProcessor != null){
            customPluginProcessors.add(springPluginProcessor);
        }
        return this;
    }

    protected final void createPluginInteractive(){
        if(pluginInteractive != null){
            return;
        }
        createPluginInteractiveOfOneself();
    }

    protected final void createPluginInteractiveOfOneself(){
        this.pluginInteractive = new PluginOneselfInteractive();
    }


    /**
     * 子类自定义插件 SpringPluginProcessor
     */
    protected void addCustomSpringPluginProcessor(){}

}
