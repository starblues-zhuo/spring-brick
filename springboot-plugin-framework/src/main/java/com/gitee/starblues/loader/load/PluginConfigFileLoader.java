package com.gitee.starblues.loader.load;

import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderExecution;
import com.gitee.starblues.utils.OrderPriority;
import org.pf4j.RuntimeMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件配置文件加载者
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class PluginConfigFileLoader implements PluginResourceLoader {

    private final String configFilePath;
    private final String fileName;
    private final RuntimeMode runtimeMode;

    public PluginConfigFileLoader(String configFilePath,
                                  String fileName,
                                  RuntimeMode runtimeMode) {
        this.configFilePath = configFilePath;
        this.fileName = fileName;
        this.runtimeMode = runtimeMode;
    }


    @Override
    public String key() {
        return null;
    }

    @Override
    public ResourceWrapper load(BasePlugin basePlugin) throws Exception {
        Resource resource;
        if(runtimeMode == RuntimeMode.DEVELOPMENT){
            // 开发环境下
            // 加载输入流
            String path = configFilePath +  fileName;
            resource = new ClassPathResource("/" + path, basePlugin.getWrapper().getPluginClassLoader());
        } else {
            // 生产环境下
            String path = configFilePath + File.separatorChar + fileName;
            resource = new FileUrlResource(path);
        }
        List<Resource> resources = new ArrayList<>();
        resources.add(resource);
        return new ResourceWrapper(resources);
    }

    @Override
    public void unload(BasePlugin basePlugin, ResourceWrapper resourceWrapper) throws Exception {
        // Do nothing
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority().down(20);
    }
}
