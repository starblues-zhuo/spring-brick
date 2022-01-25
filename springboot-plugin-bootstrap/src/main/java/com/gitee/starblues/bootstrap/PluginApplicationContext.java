package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ResourceLoader;

/**
 * 插件ApplicationContext实现
 * @author starBlues
 * @version 3.0.0
 */
public class PluginApplicationContext extends AnnotationConfigApplicationContext {

    private final PluginDescriptor pluginDescriptor;

    public PluginApplicationContext(DefaultListableBeanFactory beanFactory,
                                    ProcessorContext processorContext) {
        super(beanFactory);
        setResourceLoader(processorContext.getResourceLoader());
        this.pluginDescriptor = processorContext.getPluginDescriptor();
    }

    @Override
    public void registerShutdownHook() {
        // 忽略
    }

    @Override
    public String getApplicationName() {
        return pluginDescriptor.getPluginId();
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        super.refresh();
    }

    @Override
    public void close() {
        super.close();
    }
}
