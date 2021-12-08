package com.gitee.starblues.spring.processor.extract;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

/**
 * 插件扩展处理者
 * @author starBlues
 * @version 2.4.1
 */
public class PluginExtractPipeProcessor implements PluginPipeProcessor {

    private final ApplicationContext mainApplicationContext;
    private final SpringBeanRegister springBeanRegister;
    private final ExtractFactory extractFactory;

    public PluginExtractPipeProcessor(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
        this.springBeanRegister = new SpringBeanRegister((GenericApplicationContext) mainApplicationContext);
        this.extractFactory = ExtractFactory.getInstant();
    }

    @Override
    public void initialize() throws Exception {
        springBeanRegister.registerSingleton(ExtractFactory.class.getName(), extractFactory);
        // 获取主程序的扩展
        Map<String, Object> extractMap = mainApplicationContext.getBeansWithAnnotation(Extract.class);
        if(!extractMap.isEmpty()){
            for (Object extract : extractMap.values()) {
                extractFactory.addOfMain(extract);
            }
        }
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        Map<String, Object> extractMap = pluginApplicationContext.getBeansWithAnnotation(Extract.class);
        if(extractMap.isEmpty()){
            return;
        }
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        for (Object extract : extractMap.values()) {
            extractFactory.add(pluginId, extract);
        }
        pluginRegistryInfo.getSpringBeanRegister().registerSingleton(
                ExtractFactory.class.getName(), extractFactory
        );
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        extractFactory.remove(pluginId);
    }
}
