package com.gitee.starblues.core.launcher.plugin.involved;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 插件启动介入工厂
 * @author starBlues
 * @version 3.0.0
 */
public class PluginLaunchInvolvedFactory implements PluginLaunchInvolved{

    private static final Logger logger = LoggerFactory.getLogger(PluginLaunchInvolvedFactory.class);

    private List<PluginLaunchInvolved> pluginLaunchInvolvedList;


    @Override
    public void initialize(GenericApplicationContext applicationContext, IntegrationConfiguration configuration) {
        this.pluginLaunchInvolvedList = getPluginLaunchInvolvedList(applicationContext);
        for (PluginLaunchInvolved pluginLaunchInvolved : pluginLaunchInvolvedList) {
            try {
                pluginLaunchInvolved.initialize(applicationContext, configuration);
            } catch (Exception e){
                logger.error("[{}] execute initialize exception : {}",
                        pluginLaunchInvolved.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    protected List<PluginLaunchInvolved> getPluginLaunchInvolvedList(GenericApplicationContext applicationContext){
        List<PluginLaunchInvolved> pluginLaunchInvolvedList = getDefaultPluginLaunchInvolved();
        if(pluginLaunchInvolvedList == null){
            pluginLaunchInvolvedList = new ArrayList<>();
        }
        Map<String, PluginLaunchInvolved> pluginLaunchInvolvedMap = applicationContext.getBeansOfType(PluginLaunchInvolved.class);
        if(!ObjectUtils.isEmpty(pluginLaunchInvolvedMap)){
            pluginLaunchInvolvedList.addAll(pluginLaunchInvolvedMap.values());
        }
        pluginLaunchInvolvedList.sort(CommonUtils.orderPriority(PluginLaunchInvolved::order));
        return pluginLaunchInvolvedList;
    }

    protected List<PluginLaunchInvolved> getDefaultPluginLaunchInvolved(){
        List<PluginLaunchInvolved> defaultPluginLaunchInvolved = new ArrayList<>();
        defaultPluginLaunchInvolved.add(new DefaultPluginLaunchInvolved());
        return defaultPluginLaunchInvolved;
    }

    @Override
    public void before(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        for (PluginLaunchInvolved pluginLaunchInvolved : pluginLaunchInvolvedList) {
            pluginLaunchInvolved.before(descriptor, classLoader);
        }
    }

    @Override
    public void after(InsidePluginDescriptor descriptor, ClassLoader classLoader, SpringPluginHook pluginHook) throws Exception {
        for (PluginLaunchInvolved pluginLaunchInvolved : pluginLaunchInvolvedList) {
            pluginLaunchInvolved.after(descriptor, classLoader, pluginHook);
        }
    }

    @Override
    public void failure(InsidePluginDescriptor descriptor, ClassLoader classLoader, Throwable throwable) throws Exception {
        for (PluginLaunchInvolved pluginLaunchInvolved : pluginLaunchInvolvedList) {
            try {
                pluginLaunchInvolved.failure(descriptor, classLoader, throwable);
            } catch (Exception e){
                logger.error("[{}] execute failure exception : {}",
                        pluginLaunchInvolved.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void close(InsidePluginDescriptor descriptor, ClassLoader classLoader) throws Exception {
        for (PluginLaunchInvolved pluginLaunchInvolved : pluginLaunchInvolvedList) {
            try {
                pluginLaunchInvolved.close(descriptor, classLoader);
            } catch (Exception e){
                logger.error("[{}] execute close exception : {}",
                        pluginLaunchInvolved.getClass().getName(), e.getMessage(), e);
            }
        }
    }
}
