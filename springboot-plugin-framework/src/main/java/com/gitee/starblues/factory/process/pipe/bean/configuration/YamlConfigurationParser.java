package com.gitee.starblues.factory.process.pipe.bean.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * yaml 配置解析者
 * @author zhangzhuo
 * @version 1.0
 */
public class YamlConfigurationParser extends AbstractConfigurationParser {

    private final YAMLFactory yamlFactory;
    private final ObjectMapper objectMapper;

    public YamlConfigurationParser(IntegrationConfiguration configuration) {
        super(configuration);
        this.yamlFactory = new YAMLFactory();
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    protected Object parse(Resource resource, Class<?> pluginConfigClass)
            throws Exception{
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            YAMLParser yamlParser = yamlFactory.createParser(inputStream);
            final JsonNode node = objectMapper.readTree(yamlParser);
            if(node == null){
                return pluginConfigClass.newInstance();
            }
            TreeTraversingParser treeTraversingParser = new TreeTraversingParser(node);
            return objectMapper.readValue(treeTraversingParser, pluginConfigClass);
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
    }
}
