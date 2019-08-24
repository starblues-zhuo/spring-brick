package com.gitee.starblues.factory.process.post;

import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.bean.PluginControllerPostProcessor;
import com.gitee.starblues.factory.process.post.bean.PluginInvokePostProcessor;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件后置处理工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginPostProcessorFactory implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private List<PluginPostProcessor> pluginPostProcessors = new ArrayList<>();

    public PluginPostProcessorFactory(ApplicationContext applicationContext){
        pluginPostProcessors.add(new PluginInvokePostProcessor(applicationContext));
        pluginPostProcessors.add(new PluginControllerPostProcessor(applicationContext));
    }

    /**
     * 添加扩展
     * @param mainApplicationContext mainApplicationContext
     */
    private void addExtension(ApplicationContext mainApplicationContext) {
        ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();
        List<PluginPostProcessorExtend> pluginPostProcessorExtends = new ArrayList<>();
        extensionFactory.iteration(abstractExtension -> {
            List<PluginPostProcessorExtend> pluginPostProcessors =
                    abstractExtension.getPluginPostProcessor(mainApplicationContext);
            extensionFactory.iteration(pluginPostProcessors, pluginPipeProcessorExtend->{
                pluginPostProcessorExtends.add(pluginPipeProcessorExtend);
            });
        });
        if(pluginPostProcessorExtends.isEmpty()){
            return;
        }
        CommonUtils.order(pluginPostProcessorExtends, (pluginPipeProcessorExtend -> {
            OrderPriority order = pluginPipeProcessorExtend.order();
            if(order == null){
                order = OrderPriority.getMiddlePriority();
            }
            return order.getPriority();
        }));
        for (PluginPostProcessorExtend pluginPostProcessorExtend : pluginPostProcessorExtends) {
            pluginPostProcessors.add(pluginPostProcessorExtend);
            log.info("Register Extension PluginPostProcessor : {}", pluginPostProcessorExtend.getClass());
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
        for (PluginPostProcessor pluginPostProcessor : pluginPostProcessors) {
            pluginPostProcessor.unRegistry(pluginRegistryInfos);
        }
    }
}
