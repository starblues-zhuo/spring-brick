package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.ApplicationContextProxy;
import com.gitee.starblues.spring.SpringPluginHook;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultSpringPluginHook implements SpringPluginHook {

    private final SpringPluginProcessor pluginProcessor;
    private final ProcessorContext processorContext;

    public DefaultSpringPluginHook(SpringPluginProcessor pluginProcessor,
                                   ProcessorContext processorContext) {
        this.pluginProcessor = pluginProcessor;
        this.processorContext = processorContext;
    }

    @Override
    public void close() throws Exception{
        pluginProcessor.close(processorContext);
        processorContext.getApplicationContext().close();
        processorContext.clearRegistryInfo();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return new ApplicationContextProxy(processorContext.getApplicationContext().getBeanFactory());
    }
}
