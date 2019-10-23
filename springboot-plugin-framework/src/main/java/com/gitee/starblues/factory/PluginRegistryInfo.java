package com.gitee.starblues.factory;

import com.gitee.starblues.realize.BasePlugin;
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
     * 全局扩展信息
     */
    private static Map<String, Object> globalExtensionMap = new ConcurrentHashMap<>();

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

    public void addClasses(Class<?> aClass){
        if(aClass != null){
            classes.add(aClass);
        }
    }

    public void cleanClasses(){
        classes.clear();
    }

    public List<Class<?>> getClasses(){
        List<Class<?>> result = new ArrayList<>();
        result.addAll(classes);
        return result;
    }

    public void addGroupClasses(String key, Class<?> aClass){
        List<Class<?>> classes = groupClasses.get(key);
        if(classes == null){
            classes = new ArrayList<>();
            groupClasses.put(key, classes);
        }
        classes.add(aClass);
    }

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
        extensionMap.put(key, value);
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



    /**
     * 添加全局扩展数据
     * @param key 扩展的key
     * @param value 扩展值
     */
    public static synchronized void addGlobalExtension(String key, Object value){
        globalExtensionMap.put(key, value);
    }

    /**
     * 获取全局扩展值
     * @param key 全局扩展的key
     * @param <T> 返回值泛型
     * @return 扩展值
     */
    public static synchronized <T> T getGlobalExtension(String key){
        Object o = globalExtensionMap.get(key);
        if(o == null){
            return null;
        } else {
            return (T) o;
        }
    }

}
