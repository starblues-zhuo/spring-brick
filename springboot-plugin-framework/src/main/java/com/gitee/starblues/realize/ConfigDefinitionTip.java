package com.gitee.starblues.realize;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;

/**
 * 给系统中所有ConfigDefinition 提供一个小工具类。可通过它获取主程序的某些bean. 和当前插件一些信息
 * @author starBlues
 * @version 2.4.2
 */
public class ConfigDefinitionTip {

    private final PluginRegistryInfo pluginRegistryInfo;

    public ConfigDefinitionTip(PluginRegistryInfo pluginRegistryInfo) {
        this.pluginRegistryInfo = pluginRegistryInfo;
    }

    /**
     * 得到当前插件信息
     * @return 插件信息
     */
    public PluginInfo getCurrentPluginInfo(){
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        PluginManager pluginManager = pluginWrapper.getPluginManager();
        return new PluginInfo(pluginWrapper.getDescriptor(), pluginWrapper.getPluginState(),
                pluginWrapper.getPluginPath().toAbsolutePath().toString(),
                pluginManager.getRuntimeMode().toString());
    }

    /**
     * 得到主程序的 ApplicationContext
     * @return ApplicationContext
     */
    public ApplicationContext getMainApplication() {
        return pluginRegistryInfo.getMainApplicationContext();
    }

    /**
     * 获取当前插件的其他的ConfigDefinition
     * @param configDefinitionClass ConfigDefinition的类类型
     * @param <T> 类类型
     * @return T
     */
    public <T> T getOtherConfigDefinition(Class<T> configDefinitionClass){
        return SpringBeanUtils.getObjectClass(
                pluginRegistryInfo.getConfigSingletons(),
                configDefinitionClass);
    }

}
