package com.gitee.starblues.factory.process.pipe.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.factory.process.pipe.loader.load.PluginConfigFileLoader;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.PluginConfigUtils;
import org.pf4j.RuntimeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
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

    private final Logger logger = LoggerFactory.getLogger(SpringBootConfigFileRegistrar.class);

    private final YAMLFactory yamlFactory;
    private final ObjectMapper objectMapper;

    private static final String[] PROP_FILE_SUFFIX = new String[]{".prop", ".PROP", ".properties", ".PROPERTIES"};
    private static final String[] YML_FILE_SUFFIX = new String[]{".yml", ".YML", "yaml", "YAML"};

    public static final String CONFIG_PROP = "PLUGIN_SPRING_BOOT_CONFIG-";


    private final IntegrationConfiguration integrationConfiguration;

    public SpringBootConfigFileRegistrar(ApplicationContext mainApplicationContext){
        integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
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
        List<Resource> resources = getResource(pluginRegistryInfo);
        if(ObjectUtils.isEmpty(resources)){
            return null;
        }
        List<PropertySource<?>> propProfiles = new ArrayList<>();
        for (Resource resource : resources) {
            if(resource == null || !resource.exists()){
                continue;
            }
            String filename = resource.getFilename();
            if(ObjectUtils.isEmpty(filename)){
                return null;
            }

            for (String propFileSuffix : PROP_FILE_SUFFIX) {
                if(!filename.endsWith(propFileSuffix)){
                   continue;
                }
                List<PropertySource<?>> propertySources = getPropProfiles(resource, pluginRegistryInfo);
                if(ObjectUtils.isEmpty(propertySources)){
                    continue;
                }
                propProfiles.addAll(propertySources);
                break;
            }
            for (String propFileSuffix : YML_FILE_SUFFIX) {
                if(!filename.endsWith(propFileSuffix)){
                   continue;
                }
                List<PropertySource<?>> propertySources = getYmlProfiles(resource, pluginRegistryInfo);
                if(ObjectUtils.isEmpty(propertySources)){
                    continue;
                }
                propProfiles.addAll(propertySources);
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
        List<PropertySource<?>> propertySources = new ArrayList<>(1);
        propertySources.add(new MapPropertySource(CONFIG_PROP.concat(pluginId), result));
        return Collections.unmodifiableList(propertySources);
    }

    private List<PropertySource<?>> getPropProfiles(Resource resource,
                                                    PluginRegistryInfo pluginRegistryInfo) throws Exception{
        Properties properties = new Properties();
        properties.load(resource.getInputStream());
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        return Collections.unmodifiableList(
                Collections.singletonList(new PropertiesPropertySource(pluginId.concat("-config"), properties))
        );
    }

    private List<Resource> getResource(PluginRegistryInfo pluginRegistryInfo) throws Exception{
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        ConfigDefinition configDefinition = basePlugin.getClass().getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return null;
        }
        RuntimeMode runtimeMode = pluginRegistryInfo.getPluginWrapper().getRuntimeMode();
        String fileName = PluginConfigUtils.getConfigFileName(configDefinition, runtimeMode);
        PluginConfigFileLoader pluginConfigFileLoader = new PluginConfigFileLoader(
                integrationConfiguration.pluginConfigFilePath(), fileName
        );
        ResourceWrapper resourceWrapper = pluginConfigFileLoader.load(pluginRegistryInfo);
        return resourceWrapper.getResources();
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
