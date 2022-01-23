package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.bootstrap.processor.web.thymeleaf.PluginThymeleafProcessor;
import com.gitee.starblues.bootstrap.utils.DestroyUtils;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.ApplicationContextProxy;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.WebConfig;
import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.Map;

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
        try {
            pluginProcessor.close(processorContext);
            GenericApplicationContext applicationContext = processorContext.getApplicationContext();
            if(applicationContext != null){
                applicationContext.close();
            }
            processorContext.clearRegistryInfo();
            DestroyUtils.destroyAll(null, SpringFactoriesLoader.class, "cache", Map.class);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return new ApplicationContextProxy(processorContext.getApplicationContext().getBeanFactory());
    }

    @Override
    public WebConfig getWebConfig() {
        return processorContext.getWebConfig();
    }

    @Override
    public ThymeleafConfig getThymeleafConfig() {
        return processorContext.getRegistryInfo(PluginThymeleafProcessor.CONFIG_KEY);
    }
}
