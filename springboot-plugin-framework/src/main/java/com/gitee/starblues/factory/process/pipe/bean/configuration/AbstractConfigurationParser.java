package com.gitee.starblues.factory.process.pipe.bean.configuration;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.loader.ResourceWrapper;
import com.gitee.starblues.loader.load.PluginConfigFileLoader;
import com.gitee.starblues.realize.BasePlugin;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Objects;

/**
 * 抽象的插件配置文件解析者
 * @author starBlues
 * @version 1.0
 */
public abstract class AbstractConfigurationParser implements ConfigurationParser {

    private final IntegrationConfiguration configuration;

    public AbstractConfigurationParser(IntegrationConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

    @Override
    public Object parse(BasePlugin basePlugin, PluginConfigDefinition pluginConfigDefinition) throws Exception {
        Class<?> configClass = pluginConfigDefinition.getConfigClass();
        if(pluginConfigDefinition.getConfigClass() == null){
            throw new IllegalArgumentException("pluginConfigDefinition : " + pluginConfigDefinition + " " +
                    "configClass can not be null");
        }
        String fileName = pluginConfigDefinition.getFileName();
        if(pluginConfigDefinition.getFileName() == null || "".equals(pluginConfigDefinition.getFileName())){
            throw new IllegalArgumentException("pluginConfigDefinition : " + pluginConfigDefinition + " " +
                    "fileName can not be empty");
        }

        PluginResourceLoader resourceLoader = new PluginConfigFileLoader(
                configuration.pluginConfigFilePath(),
                fileName
        );
        ResourceWrapper resourceWrapper = resourceLoader.load(basePlugin);
        if(resourceWrapper == null){
            return null;
        }
        List<Resource> resources = resourceWrapper.getResources();
        if(resources.size() != 1){
            return null;
        }
        Object o = parse(resources.get(0), configClass);
        if(o == null){
            return configClass.newInstance();
        }
        return o;
    }


    /**
     * 子类实现解析
     * @param resource 配置文件的资源信息
     * @param pluginConfigClass 配置文件class
     * @return 返回映射后的存在值得对象
     * @throws Exception 配置文件解析异常
     */
    protected abstract Object parse(Resource resource,
                                    Class<?> pluginConfigClass) throws Exception;


}
