package com.gitee.starblues.factory.process.pipe.loader.load;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.PluginResourceLoader;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.ScanUtils;
import org.pf4j.RuntimeMode;

import java.util.Set;

/**
 * 插件类文件加载者
 *
 * @author starBlues
 * @version 2.2.2
 */
public class PluginClassLoader implements PluginResourceLoader {

    public static final String KEY = "PluginClassProcess";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public ResourceWrapper load(PluginRegistryInfo pluginRegistryInfo) throws Exception{
        RuntimeMode runtimeMode = pluginRegistryInfo.getPluginWrapper().getRuntimeMode();
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        Set<String> classPackageName = null;
        if(runtimeMode == RuntimeMode.DEPLOYMENT){
            // 生产环境
            classPackageName = ScanUtils.scanClassPackageName(
                    basePlugin.scanPackage(), basePlugin.getWrapper());

        } else if(runtimeMode == RuntimeMode.DEVELOPMENT){
            // 开发环境
            classPackageName = ScanUtils.scanClassPackageName(
                    basePlugin.scanPackage(), basePlugin.getClass());
        }
        ResourceWrapper resourceWrapper = new ResourceWrapper();
        resourceWrapper.addClassPackageNames(classPackageName);
        return resourceWrapper;
    }

    @Override
    public void unload(PluginRegistryInfo pluginRegistryInfo, ResourceWrapper resourceWrapper) throws Exception {
        // Do nothing
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }
}
