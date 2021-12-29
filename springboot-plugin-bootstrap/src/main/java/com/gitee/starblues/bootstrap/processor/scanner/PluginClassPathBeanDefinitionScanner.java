package com.gitee.starblues.bootstrap.processor.scanner;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;


/**
 * @author starBlues
 * @version 1.0
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
