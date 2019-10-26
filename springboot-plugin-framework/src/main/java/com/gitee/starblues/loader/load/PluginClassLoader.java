package com.gitee.starblues.loader.load;

import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderExecution;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 插件类文件加载者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginClassLoader implements PluginResourceLoader {

    public static final String KEY = "PluginClassProcess";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public ResourceWrapper load(BasePlugin basePlugin) throws Exception{
        String scanPackage = basePlugin.scanPackage();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(scanPackage) +
                "/**/*.class";
        ResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(basePlugin.getWrapper().getPluginClassLoader());
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        if(resources == null){
            return new ResourceWrapper();
        }
        return new ResourceWrapper(resources);
    }

    @Override
    public void unload(BasePlugin basePlugin, ResourceWrapper resourceWrapper) throws Exception {
        // Do nothing
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }
}
