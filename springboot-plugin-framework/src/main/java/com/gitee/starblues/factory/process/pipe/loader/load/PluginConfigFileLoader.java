package com.gitee.starblues.factory.process.pipe.loader.load;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.PluginResourceLoader;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 插件配置文件加载者
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginConfigFileLoader implements PluginResourceLoader {

    private final static Logger log = LoggerFactory.getLogger(PluginConfigFileLoader.class);

    private final String configFilePath;
    private final String fileName;

    public PluginConfigFileLoader(String configFilePath,
                                  String fileName) {
        this.configFilePath = configFilePath;
        this.fileName = fileName;
    }


    @Override
    public String key() {
        return null;
    }

    @Override
    public ResourceWrapper load(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<Supplier<SupplierBean>> suppliers = new ArrayList<>();
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        suppliers.add(findConfigRoot());
        suppliers.add(findPluginRoot(basePlugin));
        suppliers.add(findClassPath(pluginRegistryInfo.getPluginClassLoader()));

        for (Supplier<SupplierBean> supplier : suppliers) {
            SupplierBean supplierBean = supplier.get();
            Resource resource = supplierBean.getResource();
            if(resource.exists()){
                List<Resource> resources = new ArrayList<>();
                resources.add(resource);
                ResourceWrapper resourceWrapper = new ResourceWrapper();
                resourceWrapper.addResources(resources);
                log.info("Load the plugin '{}' config file '{}' from '{}'",
                        basePlugin.getWrapper().getPluginId(), fileName, supplierBean.getPath());
                return resourceWrapper;
            }
        }
        throw new FileNotFoundException("Not found plugin '" + basePlugin.getWrapper().getPluginId() + "' " +
                "config file : " + fileName);
    }

    @Override
    public void unload(PluginRegistryInfo pluginRegistryInfo, ResourceWrapper resourceWrapper) throws Exception {
        // Do nothing
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority().down(20);
    }

    /**
     * 从插件文件的根目录查找配置文件
     * @param basePlugin basePlugin
     * @return 返回resource
     */
    private Supplier<SupplierBean> findPluginRoot(BasePlugin basePlugin){
        return ()->{
            Path pluginPath = basePlugin.getWrapper().getPluginPath();
            String rootPath = pluginPath.getParent().toString();
            String configPath = rootPath + File.separatorChar + fileName;
            Resource resource = new FileSystemResource(configPath);
            return new SupplierBean(rootPath, resource);
        };
    }


    /**
     * 从插件配置文件 pluginConfigFilePath 的路径下查找配置文件
     * @return 返回resource
     */
    private Supplier<SupplierBean> findConfigRoot(){
        return ()->{
            String filePath = configFilePath + File.separatorChar + fileName;
            Resource resource = new FileSystemResource(filePath);
            return new SupplierBean(configFilePath, resource);
        };
    }

    /**
     * 从ClassPath 中查找配置文件
     * @param classLoader  classLoader
     * @return 返回resource
     */
    private Supplier<SupplierBean> findClassPath(ClassLoader classLoader){
        return ()->{
            Resource resource = new ClassPathResource("/" + fileName, classLoader);
            return new SupplierBean("classPath", resource);
        };
    }

    private class SupplierBean{
        private String path;
        private Resource resource;

        public SupplierBean(String path, Resource resource) {
            this.path = path;
            this.resource = resource;
        }

        public String getPath() {
            return path;
        }

        public Resource getResource() {
            return resource;
        }
    }


}
