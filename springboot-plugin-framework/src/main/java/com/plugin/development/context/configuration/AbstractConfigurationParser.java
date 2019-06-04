package com.plugin.development.context.configuration;

import com.plugin.development.exception.ConfigurationParseException;
import com.plugin.development.integration.IntegrationConfiguration;
import com.plugin.development.realize.PluginConfigDefinition;
import org.pf4j.RuntimeMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.Objects;

/**
 * @Description: 抽象的插件配置文件解析者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 14:00
 * @Update Date Time:
 * @see
 */
public abstract class AbstractConfigurationParser implements ConfigurationParser{

    private final IntegrationConfiguration configuration;

    public AbstractConfigurationParser(IntegrationConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

    @Override
    public Object parse(PluginConfigDefinition pluginConfigDefinition) throws ConfigurationParseException {
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
            Object o = parse(getResource(fileName, configClass), configClass);
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
     * @return
     * @throws ConfigurationParseException
     */
    protected abstract Object parse(Resource resource,
                                    Class<?> pluginConfigClass) throws ConfigurationParseException;


    /**
     * 获取配置文件资源
     * @param fileName 配置文件名称
     * @param aClass 配置文件对应的类class
     * @return
     * @throws Exception
     */
    private Resource getResource(String fileName, Class<?> aClass) throws Exception {
        Resource resource;

        if(configuration.environment() == RuntimeMode.DEVELOPMENT){
            // 开发环境下
            // 加载输入流
            String path = configuration.pluginConfigFilePath() +  fileName;
            resource = new ClassPathResource("/" + path, aClass.getClassLoader());
        } else {
            // 生产环境下
            String path = configuration.pluginConfigFilePath() + File.separatorChar + fileName;
            resource = new FileUrlResource(path);
        }
        return resource;
    }
}
