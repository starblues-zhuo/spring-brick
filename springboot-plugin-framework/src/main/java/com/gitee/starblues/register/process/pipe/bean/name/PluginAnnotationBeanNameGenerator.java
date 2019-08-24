package com.gitee.starblues.register.process.pipe.bean.name;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.StringUtils;

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
    private final String pluginId;

    public PluginAnnotationBeanNameGenerator(String pluginId) {
        if(pluginId == null){
            this.pluginId = "";
        } else {
            this.pluginId = pluginId + "-";
        }
    }

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefinition) {
            String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
            if (StringUtils.hasText(beanName)) {
                return beanName;
            }
        }
        return pluginId + buildDefaultBeanName(definition, registry);
    }


}
