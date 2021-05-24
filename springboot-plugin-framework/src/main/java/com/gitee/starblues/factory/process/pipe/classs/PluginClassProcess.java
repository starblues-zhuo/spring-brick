package com.gitee.starblues.factory.process.pipe.classs;

import com.gitee.starblues.extension.ExtensionInitializer;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.pipe.classs.group.*;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.factory.process.pipe.loader.load.PluginClassLoader;
import com.gitee.starblues.realize.BasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 插件类加载处理者
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginClassProcess implements PluginPipeProcessor {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * 其他类
     */
    public static final String OTHER = "other";


    private final List<PluginClassGroup> pluginClassGroups = new ArrayList<>();


    public PluginClassProcess(){}


    @Override
    public void initialize() {
        pluginClassGroups.add(new ComponentGroup());
        pluginClassGroups.add(new ControllerGroup());
        pluginClassGroups.add(new RepositoryGroup());
        pluginClassGroups.add(new ConfigDefinitionGroup());
        pluginClassGroups.add(new ConfigBeanGroup());
        pluginClassGroups.add(new SupplierGroup());
        pluginClassGroups.add(new CallerGroup());
        pluginClassGroups.add(new OneselfListenerGroup());
        pluginClassGroups.add(new WebSocketGroup());
        // 添加扩展
        pluginClassGroups.addAll(ExtensionInitializer.getClassGroupExtends());
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        ResourceWrapper resourceWrapper = pluginRegistryInfo.getPluginLoadResource(PluginClassLoader.KEY);
        if(resourceWrapper == null){
            return;
        }
        List<Resource> pluginResources = resourceWrapper.getResources();
        if(pluginResources == null){
            return;
        }
        for (PluginClassGroup pluginClassGroup : pluginClassGroups) {
            try {
                pluginClassGroup.initialize(basePlugin);
            } catch (Exception e){
                log.error("PluginClassGroup {} initialize exception. {}", pluginClassGroup.getClass(),
                        e.getMessage(), e);
            }
        }
        Set<String> classPackageNames = resourceWrapper.getClassPackageNames();
        ClassLoader classLoader = basePlugin.getWrapper().getPluginClassLoader();
        for (String classPackageName : classPackageNames) {
            Class<?> aClass = Class.forName(classPackageName, false, classLoader);
            if(aClass == null){
                continue;
            }
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

}
