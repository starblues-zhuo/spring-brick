package com.gitee.starblues.factory;

import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.Plugin;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册的插件信息
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginRegistryInfo {



    /**
     * 扩展存储项
     */
    private Map<String, Object> extensionMap = new ConcurrentHashMap<>();

    private PluginWrapper pluginWrapper;
    private BasePlugin basePlugin;

    /**
     * 插件中的Class
     */
    private List<Class<?>> classes = new ArrayList<>();
    /**
     * 插件中分类的Class
     */
    private Map<String, List<Class<?>>> groupClasses = new HashMap<>();
    private Map<String, Object> processorInfo = new HashMap<>();


    public PluginRegistryInfo(PluginWrapper pluginWrapper) {
        this.pluginWrapper = pluginWrapper;
        this.basePlugin = (BasePlugin) pluginWrapper.getPlugin();
    }

    public PluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }

    public BasePlugin getBasePlugin() {
        return basePlugin;
    }

    /**
     * 添加类到类集合容器
     * @param aClass 类
     */
    public void addClasses(Class<?> aClass){
        if(aClass != null){
            classes.add(aClass);
        }
    }

    /**
     * 清除类集合容器
     */
    public void cleanClasses(){
        classes.clear();
    }

    /**
     * 得到类集合容器
     * @return 类集合容器
     */
    public List<Class<?>> getClasses(){
        List<Class<?>> result = new ArrayList<>();
        result.addAll(classes);
        return result;
    }

    /**
     * 添加分组的类型
     * @param key 分组key
     * @param aClass 类
     */
    public void addGroupClasses(String key, Class<?> aClass){
        List<Class<?>> classes = groupClasses.get(key);
        if(classes == null){
            classes = new ArrayList<>();
            groupClasses.put(key, classes);
        }
        classes.add(aClass);
    }

    /**
     * 通过分组key得到分组中的类类型
     * @param key 处理者key
     * @return 类类型集合
     */
    public List<Class<?>> getGroupClasses(String key){
        List<Class<?>> classes = groupClasses.get(key);
        List<Class<?>> result = new ArrayList<>();
        if(classes != null){
            result.addAll(classes);
        }
        return result;
    }

    /**
     * 得到插件bean注册者信息
     * @param key 扩展的key
     * @param <T> 处理者类型
     * @return 注册者信息
     */
    public <T> T getProcessorInfo(String key){
        Object o = processorInfo.get(key);
        if(o != null){
            return (T) o;
        }
        return null;
    }

    /**
     * 添加插件bean注册者信息
     * @param key 扩展的key
     * @param value 扩展值
     */
    public void addProcessorInfo(String key, Object value){
        processorInfo.put(key, value);
    }



    /**
     * 添加扩展数据
     * @param key 扩展的key
     * @param value 扩展值
     */
    public void addExtension(String key, Object value){
        if(extensionMap.containsKey(key)){
            throw new RuntimeException("The extension key ' " + key + " 'already exists");
        }
        extensionMap.put(key, value);
    }

    /**
     * 移除扩展数据
     * @param key 扩展的key
     */
    public void removeExtension(String key){
        extensionMap.remove(key);
    }

    /**
     * 获取扩展值
     * @param key 扩展的key
     * @param <T> 返回值泛型
     * @return 扩展值
     */
    public <T> T getExtension(String key){
        Object o = extensionMap.get(key);
        if(o == null){
            return null;
        } else {
            return (T) o;
        }
    }



}
