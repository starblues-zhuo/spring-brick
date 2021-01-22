package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassProcess;
import com.gitee.starblues.factory.process.pipe.loader.PluginResourceLoadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件的pipe处理者工厂
 *
 * @author starBlues
 * @version 2.2.2
 */
public class PluginPipeProcessorFactory implements PluginPipeProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ApplicationContext applicationContext;
    private final List<PluginPipeProcessor> pluginPipeProcessors = new ArrayList<>();

    public PluginPipeProcessorFactory(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }



    @Override
    public void initialize() throws Exception{
        // 以下顺序不能更改
        // 插件资源加载者, 必须放在第一位
        pluginPipeProcessors.add(new PluginResourceLoadFactory());
        // 插件类处理者
        pluginPipeProcessors.add(new PluginClassProcess());
        // 添加前置扩展
        pluginPipeProcessors.addAll(ExtensionInitializer.getPreProcessorExtends());
        // 插件的ApplicationContext处理者
        pluginPipeProcessors.add(new PluginApplicationContextProcessor(applicationContext));
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
        boolean findException = false;
        for (PluginPipeProcessor pluginPipeProcessor : pluginPipeProcessors) {
            try {
                pluginPipeProcessor.unRegistry(pluginRegistryInfo);
            } catch (Exception e){
                findException = true;
                logger.error("unRegistry plugin '{}' failure by {}", pluginRegistryInfo.getPluginWrapper().getPluginId(),
                        pluginPipeProcessor.getClass().getName(), e);
            }
        }
        if(findException){
            throw new Exception("UnRegistry plugin '" + pluginRegistryInfo.getPluginWrapper().getPluginId() + "'failure");
        }
    }
}
