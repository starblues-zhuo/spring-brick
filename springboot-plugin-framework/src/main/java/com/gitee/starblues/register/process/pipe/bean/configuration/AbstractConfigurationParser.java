package com.gitee.starblues.register.process.pipe.bean.configuration;

import com.gitee.starblues.exception.ConfigurationParseException;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.loader.load.PluginConfigFileLoader;
import com.gitee.starblues.realize.BasePlugin;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Objects;

/**
 * 抽象的插件配置文件解析者
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractConfigurationParser implements ConfigurationParser {

    private final IntegrationConfiguration configuration;

    public AbstractConfigurationParser(IntegrationConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

    @Override
    public Object parse(BasePlugin basePlugin, PluginConfigDefinition pluginConfigDefinition) throws ConfigurationParseException {
        Class<?> configClass = pluginConfigDefinition.getConfigClass();
        if(pluginConfigDefinition.getConfigClass() == null){
            throw new ConfigurationParseException("pluginConfigDefinition : " + pluginConfigDefinition + " " +
                    "configClass can not be null");
        }
        String fileName = pluginConfigDefinition.getFileName();
        if(pluginConfigDefinition.getFileName() == null || "".equals(pluginConfigDefinition.getFileName())){
            throw new ConfigurationParseException("pluginConfigDefinition : " + pluginConfigDefinition + " " +
                    "fileName can not be empty");
        }

        try {
            PluginResourceLoader pluginResourceLoader = new PluginConfigFileLoader(
                    configuration.pluginConfigFilePath(),
                    fileName,
                    configuration.environment()
            );
            List<Resource> resources = pluginResourceLoader.load(basePlugin);
            if(resources.isEmpty() || resources.size() != 1){
                return null;
            }
            Object o = parse(resources.get(0), configClass);
            if(o == null){
                return configClass.newInstance();
            }
            return o;
        } catch (Exception e) {
            throw new ConfigurationParseException("Parse Plugin Config Failure" + e.getMessage(),e);
        }
    }


    /**
     * 子类实现解析
     * @param resource 配置文件的资源信息
     * @param pluginConfigClass 配置文件class
     * @return 返回映射后的存在值得对象
     * @throws ConfigurationParseException 配置文件解析异常
     */
    protected abstract Object parse(Resource resource,
                                    Class<?> pluginConfigClass) throws ConfigurationParseException;


}
