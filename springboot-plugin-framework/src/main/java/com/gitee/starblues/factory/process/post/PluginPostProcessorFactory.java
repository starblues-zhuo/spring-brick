package com.gitee.starblues.factory.process.post;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件后置处理工厂
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginPostProcessorFactory implements PluginPostProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final List<PluginPostProcessor> pluginPostProcessors = new ArrayList<>();
    private final ApplicationContext mainApplicationContext;

    public PluginPostProcessorFactory(ApplicationContext mainApplicationContext){
       this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public void initialize() throws Exception{
        // 以下顺序不能更改
        pluginPostProcessors.add(new PluginInvokePostProcessor(mainApplicationContext));
        pluginPostProcessors.add(new PluginControllerPostProcessor(mainApplicationContext));
        pluginPostProcessors.add(new PluginWebSocketProcessor(mainApplicationContext));
        // 主要触发启动监听事件，因此在最后一个执行。配合 OneselfListenerStopEventProcessor 该类触发启动、停止事件。
        pluginPostProcessors.add(new PluginOneselfStartEventProcessor());
        // 添加扩展
        pluginPostProcessors.addAll(ExtensionInitializer.getPostProcessorExtends());

        // 进行初始化
        for (PluginPostProcessor pluginPostProcessor : pluginPostProcessors) {
            pluginPostProcessor.initialize();
        }
    }


    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception{
        for (PluginPostProcessor pluginPostProcessor : pluginPostProcessors) {
            pluginPostProcessor.registry(pluginRegistryInfos);
        }
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception{
        boolean findException = false;
        for (PluginPostProcessor pluginPostProcessor : pluginPostProcessors) {
            try {
                pluginPostProcessor.unRegistry(pluginRegistryInfos);
            } catch (Exception e){
                findException = true;
                LOGGER.error("PluginPostProcessor '{}' unRegistry process exception",
                        pluginPostProcessor.getClass().getName(), e);
            }
        }
        if(findException){
            throw new Exception("UnRegistry plugin failure");
        }
    }
}
