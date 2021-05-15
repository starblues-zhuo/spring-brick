package com.gitee.starblues.factory.process.pipe.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.gitee.starblues.factory.PluginRegistryInfo;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 插件互相调用的bean注册者
 * @author starBlues
 * @version 2.4.3
 */
public class SpringBootConfigFileRegistrar implements PluginBeanRegistrar{

    private final YAMLFactory yamlFactory;
    private final ObjectMapper objectMapper;

    private static final String[] PROP_FILE_SUFFIX = new String[]{".prop", ".PROP", ".properties", ".PROPERTIES"};
    private static final String[] YML_FILE_SUFFIX = new String[]{".yml", ".YML", "yaml", "YAML"};

    public SpringBootConfigFileRegistrar(){
        yamlFactory = new YAMLFactory();
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        ConfigurableEnvironment environment = pluginRegistryInfo.getPluginApplicationContext().getEnvironment();
        //加载成PropertySource对象，并添加到Environment环境中
        List<PropertySource<?>> propertySources = loadProfiles(pluginRegistryInfo);
        if(ObjectUtils.isEmpty(propertySources)){
            return;
        }
        for (PropertySource<?> propertySource : propertySources) {
            environment.getPropertySources().addLast(propertySource);
        }
    }

    private List<PropertySource<?>> loadProfiles(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Resource resource = getResource(pluginRegistryInfo);
        if(resource == null){
            return null;
        }
        String filename = resource.getFilename();
        if(ObjectUtils.isEmpty(filename)){
            return null;
        }
        List<PropertySource<?>> propProfiles = null;
        for (String propFileSuffix : PROP_FILE_SUFFIX) {
            if(filename.endsWith(propFileSuffix)){
                propProfiles = getPropProfiles(resource, pluginRegistryInfo);
                break;
            }
        }
        for (String propFileSuffix : YML_FILE_SUFFIX) {
            if(filename.endsWith(propFileSuffix)){
                propProfiles = getYmlProfiles(resource, pluginRegistryInfo);
                break;
            }
        }
        return propProfiles;
    }

    private List<PropertySource<?>> getYmlProfiles(Resource resource,
                                                   PluginRegistryInfo pluginRegistryInfo) throws Exception{
        YAMLParser yamlParser = yamlFactory.createParser(resource.getInputStream());
        final JsonNode node = objectMapper.readTree(yamlParser);
        Map<String, Object> source = objectMapper.readValue(new TreeTraversingParser(node),
                new TypeReference<Map<String, Object>>(){});
        Map<String, Object> result = new HashMap<>();
        buildFlattenedMap(result, source, null);
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        return Collections.unmodifiableList(
                Collections.singletonList(new MapPropertySource(pluginId + "-config", result))
        );
    }

    private List<PropertySource<?>> getPropProfiles(Resource resource,
                                                    PluginRegistryInfo pluginRegistryInfo) throws Exception{
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        return Collections.unmodifiableList(
                Collections.singletonList(new PropertiesPropertySource(pluginId + "-config", properties))
        );
    }

    private Resource getResource(PluginRegistryInfo pluginRegistryInfo){
        ClassLoader pluginClassLoader = pluginRegistryInfo.getPluginWrapper().getPluginClassLoader();
        String springBootConfigFilePath = pluginRegistryInfo.getBasePlugin().springBootConfigFilePath();
        if(org.pf4j.util.StringUtils.isNullOrEmpty(springBootConfigFilePath)){
            return null;
        }
        Resource resource = new ClassPathResource(
                springBootConfigFilePath, pluginClassLoader
        );
        if (!resource.exists()) {
            throw new IllegalArgumentException("资源" + resource + "不存在");
        }
        return resource;
    }

    private void buildFlattenedMap(Map<String, Object> result,
                                   Map<String, Object> source,
                                   @Nullable String path) {
        source.forEach((key, value) -> {
            if (StringUtils.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                }
                else {
                    key = path + '.' + key;
                }
            }
            if (value instanceof String) {
                result.put(key, value);
            }
            else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            }
            else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                if (collection.isEmpty()) {
                    result.put(key, "");
                }
                else {
                    int count = 0;
                    for (Object object : collection) {
                        buildFlattenedMap(result, Collections.singletonMap(
                                "[" + (count++) + "]", object), key);
                    }
                }
            }
            else {
                result.put(key, (value != null ? value : ""));
            }
        });
    }

}
