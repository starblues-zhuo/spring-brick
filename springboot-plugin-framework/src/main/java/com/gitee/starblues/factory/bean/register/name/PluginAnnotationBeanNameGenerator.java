package com.gitee.starblues.factory.bean.register.name;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 *  插件注解名称生成者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginAnnotationBeanNameGenerator extends AnnotationBeanNameGenerator {

    /**
     * 插件id
     */
    public final String pluginId;

    public PluginAnnotationBeanNameGenerator(String pluginId) {
        if(pluginId == null){
            this.pluginId = "";
        } else {
            this.pluginId = pluginId + "-";
        }

    }

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        return pluginId + super.generateBeanName(definition, registry);
    }
}
