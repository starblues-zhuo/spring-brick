package com.gitee.starblues.factory.bean;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.factory.FactoryType;
import com.gitee.starblues.factory.MainFactoryType;
import com.gitee.starblues.factory.PluginFactory;
import com.gitee.starblues.factory.bean.register.*;
import com.gitee.starblues.loader.PluginResourceLoadFactory;
import com.gitee.starblues.loader.load.PluginClassLoader;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderExecution;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的插件bean工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultPluginBeanFactory implements PluginFactory {

    private final static Logger LOG = LoggerFactory.getLogger(DefaultPluginBeanFactory.class);

    private final static Map<String, PluginSpringBean>
            PLUGIN_BEAN_MAP = new ConcurrentHashMap<>();

    private final List<PluginBeanRegister> otherPluginBeanRegisters = new ArrayList<>(5);
    private final PluginBasicBeanRegister pluginBasicBeanRegister;

    public DefaultPluginBeanFactory(ApplicationContext mainApplicationContext){
        try {
            this.pluginBasicBeanRegister = new PluginBasicBeanRegister(mainApplicationContext);
            otherPluginBeanRegisters.add(new PluginConfigBeanRegister(mainApplicationContext));
            otherPluginBeanRegisters.add(new PluginControllerBeanRegister(mainApplicationContext,
                    pluginBasicBeanRegister));
            addExtension(mainApplicationContext);
        } catch (PluginBeanFactoryException e){
            throw new RuntimeException(e);
        }
        CommonUtils.order(otherPluginBeanRegisters, (pluginBeanRegister -> pluginBeanRegister.order()));
    }

    /**
     * 添加扩展
     * @param mainApplicationContext mainApplicationContext
     */
    private void addExtension(ApplicationContext mainApplicationContext) {
        ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();
        Map<String, List<AbstractExtension>> pluginBeanRegisterMap = extensionFactory.getPluginExtension();
        pluginBeanRegisterMap.forEach((k, abstractExtensions)->{
            for (AbstractExtension abstractExtension : abstractExtensions) {
                List<PluginBeanRegister> pluginBeanRegister = abstractExtension
                        .getPluginBeanRegister(mainApplicationContext);
                if(pluginBeanRegister != null || !pluginBeanRegister.isEmpty()){
                    otherPluginBeanRegisters.addAll(pluginBeanRegister);
                }
            }
            LOG.info("Register Extension PluginBeanRegister : {}", k);
        });
    }


    @Override
    public FactoryType factoryType() {
        return MainFactoryType.BEAN;
    }

    @Override
    public synchronized void registry(PluginWrapper pluginWrapper) throws PluginBeanFactoryException {
        BasePlugin basePlugin = getBasePlugin(pluginWrapper);
        List<Class<?>> pluginClass = loadPluginClass(basePlugin);
        if(pluginClass.isEmpty()){
            LOG.warn("plugin {} not found class", pluginWrapper.getPluginId());
            return;
        }
        PluginSpringBean pluginSpringBean = PLUGIN_BEAN_MAP.get(pluginWrapper.getPluginId());
        if(pluginSpringBean == null){
            pluginSpringBean = new PluginSpringBean();
            PLUGIN_BEAN_MAP.put(pluginWrapper.getPluginId(), pluginSpringBean);
        }
        List<Class<?>> otherClass = new ArrayList<>();
        // 先注册基本的Class
        for (Class<?> aClass : pluginClass) {
            if(aClass == null){
                continue;
            }
            if(pluginBasicBeanRegister.support(basePlugin, aClass)){
                Object registry = pluginBasicBeanRegister.registry(basePlugin, aClass);
                if(registry == null){
                    continue;
                }
                pluginSpringBean.addPluginRegister(pluginBasicBeanRegister.key(), registry);
            } else {
                otherClass.add(aClass);
            }
        }
        // 再注册其他的Class
        for (Class<?> aClass : otherClass) {
            for (PluginBeanRegister pluginBeanRegister : otherPluginBeanRegisters) {
                if(!pluginBeanRegister.support(basePlugin, aClass)){
                    continue;
                }
                Object registry = pluginBeanRegister.registry(basePlugin, aClass);
                if(registry == null){
                    continue;
                }
                pluginSpringBean.addPluginRegister(pluginBeanRegister.key(), registry);
            }
        }
    }


    /**
     * 加载插件类
     * @param basePlugin basePlugin
     * @return List
     * @throws PluginBeanFactoryException PluginBeanFactoryException
     */
    private List<Class<?>> loadPluginClass(BasePlugin basePlugin)
            throws PluginBeanFactoryException{
        PluginResourceLoadFactory pluginResourceLoadFactory = basePlugin.getPluginResourceLoadFactory();
        List<Resource> pluginResources = pluginResourceLoadFactory.getPluginResources(PluginClassLoader.KEY);
        if(pluginResources == null){
            return Collections.emptyList();
        }
        List<Class<?>> pluginClass = new ArrayList<>();
        try {
            for (Resource pluginResource : pluginResources) {
                String path = pluginResource.getURL().getPath();
                String packageName = path.substring(0, path.indexOf(".class"))
                        .replace("/", ".");
                packageName = packageName.substring(packageName.indexOf(basePlugin.scanPackage()));
                Class<?> c1 = Class.forName(packageName, false,
                        basePlugin.getWrapper().getPluginClassLoader());
                pluginClass.add(c1);
            }
            return pluginClass;
        } catch (Exception e){
            e.printStackTrace();
            throw new PluginBeanFactoryException(e);
        }
    }

    @Override
    public synchronized void unRegistry(PluginWrapper pluginWrapper) throws PluginBeanFactoryException {
        BasePlugin basePlugin = getBasePlugin(pluginWrapper);
        PluginSpringBean pluginSpringBean = PLUGIN_BEAN_MAP.get(basePlugin.getWrapper().getPluginId());
        if(pluginSpringBean == null){
            throw new PluginBeanFactoryException("Not found plugin <"
                    + pluginWrapper.getPluginId() + "> registry info!");
        }
        Object basicPluginRegister = pluginSpringBean.getPluginRegister(pluginBasicBeanRegister.key());
        if(basicPluginRegister != null){
            pluginBasicBeanRegister.unRegistry(basePlugin, basicPluginRegister.toString());
        }
        for (PluginBeanRegister pluginBeanRegister : otherPluginBeanRegisters) {
            if(pluginBeanRegister == null){
                continue;
            }
            Object pluginRegister = pluginSpringBean.getPluginRegister(pluginBeanRegister.key());
            if(pluginRegister != null){
                pluginBeanRegister.unRegistry(basePlugin, pluginRegister);
            }
        }
    }

    @Override
    public int order() {
        return OrderExecution.MIDDLE;
    }

    /**
     * 添加插件bean注册者
     * @param pluginBasicBeanRegister pluginBasicBeanRegister
     */
    public synchronized void addPluginBeanRegister(PluginBasicBeanRegister pluginBasicBeanRegister){
        if(pluginBasicBeanRegister != null){
            otherPluginBeanRegisters.add(pluginBasicBeanRegister);
        }
    }


    /**
     * 得到BasePlugin
     * @param pluginWrapper pluginWrapper
     * @return BasePlugin
     * @throws  PluginBeanFactoryException PluginBeanFactoryException
     */
    private BasePlugin getBasePlugin(PluginWrapper pluginWrapper) throws PluginBeanFactoryException {
        if(pluginWrapper == null){
            throw new PluginBeanFactoryException("pluginWrapper can not be null");
        }
        Plugin plugin = pluginWrapper.getPlugin();
        if(!(plugin instanceof BasePlugin)){
            throw new PluginBeanFactoryException("plugin type error. There need BasePlugin. " +
                    "but plugin is " + plugin);
        }
        return (BasePlugin) plugin;
    }



    /**
     * 插件bean定义
     */
    private class PluginSpringBean {
        private Map<String, Object> pluginRegisters = new HashMap<>();

        public void addPluginRegister(String key, Object object){
            if(object == null){
                return;
            }
            if(!pluginRegisters.containsKey(key)){
                pluginRegisters.put(key, object);
            }
        }

        public Object getPluginRegister(String key) {
            return pluginRegisters.get(key);
        }

    }


}
