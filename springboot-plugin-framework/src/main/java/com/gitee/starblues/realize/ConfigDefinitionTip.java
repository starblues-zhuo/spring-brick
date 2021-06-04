package com.gitee.starblues.realize;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 将Springboot类型的配置文件中的值映射为bean
     * 注意: 只针对插件扩展的 springboot 配置文件生效
     * @param prefix 配置文件中的前置, 比如: plugin.config
     * @param type 配置文件中结构对应的类类型, 比如: plugin.config 下定义的键值对和type类类型一致
     * @param <T> 类类型
     * @return T
     */
    public <T> T getConfigOfBean(String prefix, Class<T> type){
        return pluginRegistryInfo.getPluginBinder()
                .bind(prefix, Bindable.of(type))
                .orElseGet(() -> null);
    }

    /**
     * 将Springboot类型的配置文件中的值映射为 List
     * 注意: 只针对插件扩展的 springboot 配置文件生效
     * @param prefix 配置文件中的前置, 比如: plugin.config
     * @param type List元素的类类型
     * @param <T> List中定义的类类型
     * @return List
     */
    public <T> List<T> getConfigOfList(String prefix, Class<T> type){
        return pluginRegistryInfo.getPluginBinder()
                .bind(prefix, Bindable.listOf(type))
                .orElseGet(() -> Collections.emptyList());
    }

    /**
     * 将Springboot类型的配置文件中的值映射为 Set
     * 注意: 只针对插件扩展的 springboot 配置文件生效
     * @param prefix 配置文件中的前置, 比如: plugin.config
     * @param type Set元素的类类型
     * @param <T> 类类型
     * @return Set
     */
    public <T> Set<T> getConfigOfSet(String prefix, Class<T> type){
        return pluginRegistryInfo.getPluginBinder()
                .bind(prefix, Bindable.setOf(type))
                .orElseGet(() -> Collections.emptySet());
    }


    /**
     * 将Springboot类型的配置文件中的值映射为 Map
     * 注意: 只针对插件扩展的 springboot 配置文件生效
     * @param prefix 配置文件中的前置, 比如: plugin.config
     * @param keyType map的key元素类型
     * @param valueType map的值元素类型
     * @param <K> map key 元素的类类型
     * @param <V> map value 元素的类类型
     * @return Map
     */
    public <K, V> Map<K, V> getConfigOfSet(String prefix, Class<K> keyType, Class<V> valueType){
        return pluginRegistryInfo.getPluginBinder()
                .bind(prefix, Bindable.mapOf(keyType, valueType))
                .orElseGet(() -> Collections.emptyMap());
    }


    /**
     * 返回当前插件的ConfigurableEnvironment
     * @return ConfigurableEnvironment
     */
    public ConfigurableEnvironment getPluginEnvironment(){
        return pluginRegistryInfo.getPluginApplicationContext().getEnvironment();
    }

    /**
     * 返回当前插件的Binder
     * @return Binder
     */
    public Binder getPluginBinder(){
        return pluginRegistryInfo.getPluginBinder();
    }

}
