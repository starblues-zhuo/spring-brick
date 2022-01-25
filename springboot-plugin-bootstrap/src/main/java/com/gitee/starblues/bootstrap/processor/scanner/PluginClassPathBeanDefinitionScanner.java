package com.gitee.starblues.bootstrap.processor.scanner;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;


/**
 * 插件自定义 classpath bean 扫描
 * @author starBlues
 * @version 3.0.0
 */
public class PluginClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public PluginClassPathBeanDefinitionScanner(ProcessorContext processorContext) {
        this(processorContext, true);
    }

    public PluginClassPathBeanDefinitionScanner(ProcessorContext processorContext, boolean useDefaultFilters) {
        super(processorContext.getApplicationContext(), useDefaultFilters,
                processorContext.getApplicationContext().getEnvironment(),
                processorContext.getResourceLoader());
    }

}
