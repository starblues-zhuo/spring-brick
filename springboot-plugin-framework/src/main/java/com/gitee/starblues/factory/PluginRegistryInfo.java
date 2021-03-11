package com.gitee.starblues.factory;

import com.gitee.starblues.factory.process.pipe.PluginInfoContainers;
import com.gitee.starblues.factory.process.pipe.PluginPipeApplicationContextProcessor;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.*;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ClassUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册的插件信息
 *
 * @author starBlues
 * @version 2.4.1
 */
public class PluginRegistryInfo {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PluginWrapper pluginWrapper;
    private final PluginManager pluginManager;
    private final GenericApplicationContext mainApplicationContext;
    private final AnnotationConfigApplicationContext pluginApplicationContext;
    private final SpringBeanRegister springBeanRegister;

    /**
     * 是否跟随主程序启动而初始化
     */
    private final boolean followingInitial;
    private final BasePlugin basePlugin;


    /**
     * 扩展存储项
     */
    private final Map<String, Object> extensionMap = new ConcurrentHashMap<>();

    /**
     * 插件中的配置单例bean
     */
    private final Set<Object> configSingletonObjects = new HashSet<>(4);

    /**
     * 插件中的Class
     */
    private final List<Class<?>> classes = new ArrayList<>(8);

    /**
     * 插件加载的资源
     */
    private final Map<String, ResourceWrapper> pluginLoadResources = new ConcurrentHashMap<>(8);

    /**
     * 插件中分类的Class
     */
    private final Map<String, List<Class<?>>> groupClasses = new ConcurrentHashMap<>(8);

    /**
     * 处理者信息
     */
    private final Map<String, Object> processorInfo = new ConcurrentHashMap<>(8);

    /**
     * websocket路径
     */
    private final List<String> websocketPaths = new ArrayList<>();

    private PluginRegistryInfo(PluginWrapper pluginWrapper,
                               PluginManager pluginManager,
                               GenericApplicationContext mainApplicationContext,
                               boolean followingInitial) {
        this.pluginWrapper = pluginWrapper;
        this.pluginManager = pluginManager;
        this.basePlugin = (BasePlugin) pluginWrapper.getPlugin();
        this.mainApplicationContext = mainApplicationContext;
        this.followingInitial = followingInitial;

        // 生成插件Application
        this.pluginApplicationContext = new AnnotationConfigApplicationContext();
        this.pluginApplicationContext.setClassLoader(basePlugin.getWrapper().getPluginClassLoader());
        this.springBeanRegister = new SpringBeanRegister(pluginApplicationContext);
    }

    public static PluginRegistryInfo build(PluginWrapper pluginWrapper,
                                           PluginManager pluginManager,
                                           GenericApplicationContext parentApplicationContext,
                                           boolean followingInitial){
        Objects.requireNonNull(pluginWrapper, "PluginWrapper can't is null");
        Objects.requireNonNull(pluginWrapper, "PluginManager can't is null");
        Objects.requireNonNull(pluginWrapper, "parentApplicationContext can't is null");
        return new PluginRegistryInfo(pluginWrapper, pluginManager,
                parentApplicationContext, followingInitial);
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
        return Collections.unmodifiableList(classes);
    }

    /**
     * 添加插件中加载的资源
     * @param key key
     * @param resourceWrapper 资源包装者
     */
    public void addPluginLoadResource(String key, ResourceWrapper resourceWrapper){
        if(StringUtils.isNullOrEmpty(key)){
            return;
        }
        if(resourceWrapper == null){
            return;
        }
        pluginLoadResources.put(key, resourceWrapper);
    }

    /**
     * 得到插件中加载的资源
     * @param key 资源key
     * @return ResourceWrapper
     */
    public ResourceWrapper getPluginLoadResource(String key) {
        return pluginLoadResources.get(key);
    }

    /**
     * 添加分组的类型
     * @param key 分组key
     * @param aClass 类
     */
    public void addGroupClasses(String key, Class<?> aClass){
        if(StringUtils.isNullOrEmpty(key)){
            return;
        }
        if(aClass == null){
            return;
        }
        List<Class<?>> classes = groupClasses.computeIfAbsent(key, k -> new ArrayList<>());
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
     * 添加插件中的配置对象
     * @param singletonObject 单例对象
     */
    public void addConfigSingleton(Object singletonObject){
        configSingletonObjects.add(singletonObject);
    }

    /**
     * 添加插件中的配置对象
     * @return 配置的实现对象
     */
    public Set<Object> getConfigSingletons(){
        return Collections.unmodifiableSet(configSingletonObjects);
    }

    /**
     * 添加处理者信息
     * @param key key
     * @param value value
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
     * 得到主程序的ApplicationContext
     * @return GenericApplicationContext
     */
    public GenericApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    /**
     * 得到当前插件的ApplicationContext
     * @return AnnotationConfigApplicationContext
     */
    public GenericApplicationContext getPluginApplicationContext() {
        return pluginApplicationContext;
    }

    /**
     * 得到当前插件Bean注册者
     * @return SpringBeanRegister
     */
    public SpringBeanRegister getSpringBeanRegister() {
        return springBeanRegister;
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

    public ClassLoader getPluginClassLoader(){
        return pluginWrapper.getPluginClassLoader();
    }

    public boolean isFollowingInitial() {
        return followingInitial;
    }


    void destroy(){
        // 关闭ApplicationContext
        try {
            PluginInfoContainers.removePluginApplicationContext(getPluginWrapper().getPluginId());
            closePluginApplicationContext();
        } catch (Exception e){
            logger.error("Close plugin '{}'-ApplicationContext failure", getPluginWrapper().getPluginId(), e);
        }

        // 清除数据集合
        try {
            extensionMap.clear();
            classes.clear();
            groupClasses.clear();
            processorInfo.clear();
            pluginLoadResources.clear();
            configSingletonObjects.clear();
        } catch (Exception e){
            logger.error("Clear plugin '{}' failure", getPluginWrapper().getPluginId(), e);
        }
    }

    private void closePluginApplicationContext() {
        try {
            getSpringBeanRegister().destroySingletons();
            pluginApplicationContext.close();
        } catch (Exception e){
            logger.error("Close plugin '{}' ApplicationContext failure", getPluginWrapper().getPluginId(), e);
        }
    }

    public void addWebsocketPath(String path) {
        websocketPaths.add(path);
    }

    public List<String> getWebsocketPaths() {
        return websocketPaths;
    }

}
