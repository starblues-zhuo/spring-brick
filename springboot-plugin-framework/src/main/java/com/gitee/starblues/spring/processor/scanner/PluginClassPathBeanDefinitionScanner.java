package com.gitee.starblues.spring.processor.scanner;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;


/**
 * @author starBlues
 * @version 1.0
 */
public class PluginClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public PluginClassPathBeanDefinitionScanner(SpringPluginRegistryInfo registryInfo) {
        this(registryInfo, true);
    }

    public PluginClassPathBeanDefinitionScanner(SpringPluginRegistryInfo registryInfo, boolean useDefaultFilters) {
        super(registryInfo.getPluginSpringApplication().getApplicationContext(), useDefaultFilters,
                registryInfo.getPluginSpringApplication().getApplicationContext().getEnvironment(),
                registryInfo.getPluginSpringApplication().getResourceLoader());
    }

}
