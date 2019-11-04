package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.BasicBeanProcessor;
import com.gitee.starblues.factory.process.pipe.bean.ConfigBeanProcessor;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件的pipe处理者工厂
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginPipeProcessorFactory implements PluginPipeProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ApplicationContext applicationContext;
    private final List<PluginPipeProcessor> pluginPipeProcessors = new ArrayList<>();

    public PluginPipeProcessorFactory(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }



    @Override
    public void initialize() throws Exception{
        pluginPipeProcessors.add(new PluginClassProcess());
        pluginPipeProcessors.add(new BasicBeanProcessor(applicationContext));
        pluginPipeProcessors.add(new ConfigBeanProcessor(applicationContext));
        // 添加扩展
        pluginPipeProcessors.addAll(ExtensionInitializer.getPipeProcessorExtends());

        // 进行初始化
        for (PluginPipeProcessor pluginPipeProcessor : pluginPipeProcessors) {
            pluginPipeProcessor.initialize();
        }
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        for (PluginPipeProcessor pluginPipeProcessor : pluginPipeProcessors) {
            pluginPipeProcessor.registry(pluginRegistryInfo);
        }
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        for (PluginPipeProcessor pluginPipeProcessor : pluginPipeProcessors) {
            pluginPipeProcessor.unRegistry(pluginRegistryInfo);
        }
    }
}
