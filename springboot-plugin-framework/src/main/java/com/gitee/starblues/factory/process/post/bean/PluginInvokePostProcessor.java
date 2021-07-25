package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.InvokeBeanRegistrar;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 处理插件中类之间相互调用的的功能. 主要获取被调用者的对象,  然后存储到被调用者容器中
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginInvokePostProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public PluginInvokePostProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            try {
                Set<String> supperBeanNames = pluginRegistryInfo.getExtension(InvokeBeanRegistrar.SUPPLIER_KEY);
                if(supperBeanNames == null || supperBeanNames.isEmpty()){
                    continue;
                }
                GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
                for (String supperBeanName : supperBeanNames) {
                    if(pluginApplicationContext.containsBean(supperBeanName)){
                        Object bean = pluginApplicationContext.getBean(supperBeanName);
                        InvokeBeanRegistrar.addSupper(pluginId, supperBeanName, bean);
                    }
                }
            } catch (Exception e){
                log.error("Process plugin '{}' supper bean exception.", pluginId, e);
            }
        }
    }


    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception{
        // 什么也不做
    }



}
