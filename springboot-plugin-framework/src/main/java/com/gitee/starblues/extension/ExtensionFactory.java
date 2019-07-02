package com.gitee.starblues.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扩展工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ExtensionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionFactory.class);

    private static final ExtensionFactory EXTENSION_FACTORY = new ExtensionFactory();

    private final Map<String, List<AbstractExtension>> pluginExtensionMap = new HashMap<>();

    private ExtensionFactory(){}

    public static ExtensionFactory getSingleton(){
        return EXTENSION_FACTORY;
    }

    public void addExtension(AbstractExtension abstractExtension){
        if(abstractExtension == null){
            LOG.warn("add failure, abstractExtension is null");
            return;
        }
        String key = abstractExtension.key();
        if(StringUtils.isEmpty(key)){
            LOG.error("add failure, key is empty");
        }
        List<AbstractExtension> abstractExtensions = pluginExtensionMap.get(key);
        if(abstractExtensions == null){
            abstractExtensions = new ArrayList<>();
            pluginExtensionMap.put(key, abstractExtensions);
        }
        abstractExtensions.add(abstractExtension);
    }


    public Map<String, List<AbstractExtension>> getPluginExtension() {
        return pluginExtensionMap;
    }


}
