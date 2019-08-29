package com.gitee.starblues.factory.process.pipe.classs;

import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.loader.PluginResourceLoadFactory;
import com.gitee.starblues.loader.load.PluginClassLoader;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件类加载处理者
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginClassProcess implements PluginPipeProcessor {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 其他类
     */
    public static final String OTHER = "other";


    private final List<PluginClassGroup> pluginClassGroups = new ArrayList<>();


    public PluginClassProcess(ApplicationContext applicationContext){
        pluginClassGroups.add(new ComponentGroup());
        pluginClassGroups.add(new ControllerGroup());
        pluginClassGroups.add(new RepositoryGroup());
        pluginClassGroups.add(new ConfigurationGroup());
        pluginClassGroups.add(new ConfigDefinitionGroup());
        pluginClassGroups.add(new SupplierGroup());
        pluginClassGroups.add(new CallerGroup());
        addExtension(applicationContext);
    }

    /**
     * 添加扩展
     * @param applicationContext applicationContext
     */
    private void addExtension(ApplicationContext applicationContext) {
        ExtensionFactory extensionFactory = ExtensionFactory.getSingleton();
        extensionFactory.iteration(abstractExtension -> {
            List<PluginClassGroupExtend> pluginClassGroups = abstractExtension.getPluginClassGroup(applicationContext);
            extensionFactory.iteration(pluginClassGroups, pluginClassGroup -> {
                this.pluginClassGroups.add(pluginClassGroup);
                log.info("Register Extension PluginClassGroup : {}", pluginClassGroup.key());
            });
        });
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        PluginResourceLoadFactory pluginResourceLoadFactory = basePlugin.getPluginResourceLoadFactory();
        List<Resource> pluginResources = pluginResourceLoadFactory.getPluginResources(PluginClassLoader.KEY);
        if(pluginResources == null){
            return;
        }
        CustomClassLoader classLoader = new CustomClassLoader();
        for (Resource pluginResource : pluginResources) {
            String path = pluginResource.getURL().getPath();
            String packageName = path.substring(0, path.indexOf(".class"))
                    .replace("/", ".");
            packageName = packageName.substring(packageName.indexOf(basePlugin.scanPackage()));
            Class<?> aClass = Class.forName(packageName, false, basePlugin.getWrapper().getPluginClassLoader());
            //            System.out.println(pluginResource.getURL());
//            URLClassLoader myloader =  new URLClassLoader( new URL[]{pluginResource.getURL()}, this.getClass().getClassLoader());
//            myloader.loadClass(packageName);

            boolean findGroup = false;
            for (PluginClassGroup pluginClassGroup : pluginClassGroups) {
                if(pluginClassGroup == null || StringUtils.isEmpty(pluginClassGroup.groupId())){
                    continue;
                }
                if(pluginClassGroup.filter(aClass)){
                    pluginRegistryInfo.addGroupClasses(pluginClassGroup.groupId(), aClass);
                    findGroup = true;
                }
            }
            if(!findGroup){
                pluginRegistryInfo.addGroupClasses(OTHER, aClass);
            }
            pluginRegistryInfo.addClasses(aClass);
        }
    }

    @Override
    public void unRegistry(PluginRegistryInfo registerPluginInfo) throws Exception {
        registerPluginInfo.cleanClasses();
    }

    class CustomClassLoader extends ClassLoader{
        private Map<String, Class> resourceMap = new ConcurrentHashMap<>();

        public CustomClassLoader() {
        }

        public void setResource(String packageName, Class resource) {
            this.resourceMap.put(packageName, resource);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
           return resourceMap.get(name);
        }
    }


}
