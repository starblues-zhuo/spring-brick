package com.gitee.starblues.extension;

import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 静态的扩展工厂
 *
 * @author starBlues
 * @version 2.4.0
 */
public class ExtensionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ExtensionFactory.class);

    /**
     * 扩展工厂。key 扩展的key. 值为扩展的实现
     */
    private final static Map<String, AbstractExtension> PLUGIN_EXTENSION_MAP = new ConcurrentHashMap<>();

    private ExtensionFactory(){}



    public static void addExtension(AbstractExtension abstractExtension){
        if(abstractExtension == null){
            LOG.warn("add failure, abstractExtension is null");
            return;
        }
        String key = abstractExtension.key();
        if(StringUtils.isEmpty(key)){
            LOG.error("add failure, key is empty");
        }
        PLUGIN_EXTENSION_MAP.put(key, abstractExtension);
    }

    /**
     * 得到PluginControllerProcessorExtend的实现
     * @param mainApplicationContext 主程序的 ApplicationContext
     * @return PluginControllerProcessorExtend 的实现对象集合
     */
    public static List<PluginControllerProcessorExtend> getPluginControllerProcessorExtend(ApplicationContext mainApplicationContext){
        return SpringBeanUtils.getBeans(mainApplicationContext,
                PluginControllerProcessorExtend.class);
    }


    static Map<String, AbstractExtension> getPluginExtension() {
        return Collections.unmodifiableMap(PLUGIN_EXTENSION_MAP);
    }




}
