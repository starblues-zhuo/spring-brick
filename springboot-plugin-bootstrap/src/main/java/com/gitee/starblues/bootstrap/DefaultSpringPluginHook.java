package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.SpringPluginHook;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultSpringPluginHook implements SpringPluginHook {

    private final SpringPluginProcessor springPluginProcessor;
    private final ProcessorContext processorContext;

    public DefaultSpringPluginHook(SpringPluginProcessor springPluginProcessor,
                                   ProcessorContext processorContext) {
        this.springPluginProcessor = springPluginProcessor;
        this.processorContext = processorContext;
    }

    @Override
    public Object getGenericApplicationContext() {
        return processorContext.getApplicationContext();
    }

    @Override
    public void close() throws Exception{
        springPluginProcessor.close(processorContext);
        processorContext.getApplicationContext().close();
        processorContext.clearRegistryInfo();
    }
}
