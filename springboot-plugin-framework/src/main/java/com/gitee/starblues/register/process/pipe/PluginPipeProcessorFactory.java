package com.gitee.starblues.register.process.pipe;

import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.register.PluginRegistryInfo;
import com.gitee.starblues.register.process.pipe.bean.BasicBeanProcessor;
import com.gitee.starblues.register.process.pipe.bean.ConfigBeanProcessor;
import com.gitee.starblues.register.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.register.process.pipe.classs.PluginClassProcess;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件的pipe处理者工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginPipeProcessorFactory implements PluginPipeProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private List<PluginPipeProcessor> pluginPipeProcessors = new ArrayList<>();

    public PluginPipeProcessorFactory(ApplicationContext applicationContext){
        pluginPipeProcessors.add(new PluginClassProcess(applicationContext));
        pluginPipeProcessors.add(new BasicBeanProcessor(applicationContext));
        pluginPipeProcessors.add(new ConfigBeanProcessor(applicationContext));
        addExtension(applicationContext);
    }

    /**
     * 添加扩展
     * @param applicationContext applicationContext
     */
    private void addExtension(ApplicationContext applicationContext) {
        ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();
        List<PluginPipeProcessorExtend> pluginPipeProcessorExtends = new ArrayList<>();
        extensionFactory.iteration(abstractExtension -> {
            List<PluginPipeProcessorExtend> pluginPipeProcessors =
                    abstractExtension.getPluginPipeProcessor(applicationContext);
            extensionFactory.iteration(pluginPipeProcessors, pluginPipeProcessorExtend->{
                pluginPipeProcessorExtends.add(pluginPipeProcessorExtend);
            });
        });
        if(pluginPipeProcessorExtends.isEmpty()){
            return;
        }
        CommonUtils.order(pluginPipeProcessorExtends, (pluginPipeProcessorExtend -> {
            OrderPriority order = pluginPipeProcessorExtend.order();
            if(order == null){
                order = OrderPriority.getMiddlePriority();
            }
            return order.getPriority();
        }));
        for (PluginPipeProcessorExtend pluginPipeProcessorExtend : pluginPipeProcessorExtends) {
            pluginPipeProcessors.add(pluginPipeProcessorExtend);
            log.info("Register Extension PluginPipeProcessor : {}", pluginPipeProcessorExtend.key());
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
