package com.gitee.starblues.extension.resources.thymeleaf;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Thymeleaf 处理者
 * @author starBlues
 * @version 2.4.0
 */
public class ThymeleafProcessor implements PluginPipeProcessorExtend {

    private static final String TEMPLATE_RESOLVER_BEAN = "ClassLoaderTemplateResolver";
    private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafProcessor.class);

    public ThymeleafProcessor() {
    }

    @Override
    public String key() {
        return "ThymeleafProcessor";
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getMiddlePriority();
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        SpringTemplateEngine springTemplateEngine = getSpringTemplateEngine(pluginRegistryInfo);
        if(springTemplateEngine == null){
            return;
        }
        SpringBootThymeleafConfig config = SpringBeanUtils.getObjectByInterfaceClass(
                pluginRegistryInfo.getConfigSingletons(),
                SpringBootThymeleafConfig.class);
        if(config == null){
            return;
        }
        ThymeleafConfig thymeleafConfig = new ThymeleafConfig();
        config.config(thymeleafConfig);
        String prefix = thymeleafConfig.getPrefix();
        if(StringUtils.isEmpty(prefix)){
            throw new IllegalArgumentException("prefix can't be empty");
        } else {
            if(!prefix.endsWith("/")){
                thymeleafConfig.setPrefix(prefix + "/");
            }
        }
        if(StringUtils.isEmpty(thymeleafConfig.getSuffix())){
            throw new IllegalArgumentException("suffix can't be empty");
        }

        if(thymeleafConfig.getMode() == null){
            throw new IllegalArgumentException("mode can't be null");
        }

        ClassLoader pluginClassLoader = pluginRegistryInfo.getPluginWrapper().getPluginClassLoader();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(
                pluginClassLoader
        );
        resolver.setPrefix(thymeleafConfig.getPrefix() + "/");
        resolver.setSuffix(thymeleafConfig.getSuffix());
        resolver.setTemplateMode(thymeleafConfig.getMode());

        resolver.setCacheable(thymeleafConfig.isCache());
        if(thymeleafConfig.getEncoding() != null){
            resolver.setCharacterEncoding(thymeleafConfig.getEncoding().name());
        }
        Integer order = thymeleafConfig.getTemplateResolverOrder();
        if(order != null){
            resolver.setOrder(order);
        }
        resolver.setCheckExistence(true);
        Set<ITemplateResolver> templateResolvers = getITemplateResolvers(springTemplateEngine);
        if(templateResolvers != null){
            templateResolvers.add(resolver);
        } else {
            LOGGER.error("You can't use Thymeleaf, because not fount 'Set<ITemplateResolver>' " +
                    "from Bean:SpringTemplateEngine by reflect");
            return;
        }
        pluginRegistryInfo.addExtension(TEMPLATE_RESOLVER_BEAN, resolver);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Object resolver = pluginRegistryInfo.getExtension(TEMPLATE_RESOLVER_BEAN);
        if(resolver == null){
            return;
        }
        try {
            SpringTemplateEngine springTemplateEngine = getSpringTemplateEngine(pluginRegistryInfo);
            Set<ITemplateResolver> templateResolvers = getITemplateResolvers(springTemplateEngine);
            if(templateResolvers != null && resolver instanceof ClassLoaderTemplateResolver){
                templateResolvers.remove(resolver);
            }
        } catch (Exception e){
            LOGGER.error("unRegistry plugin '{}' templateResolver failure",
                    pluginRegistryInfo.getPluginWrapper().getPluginId(),e);
        }
    }

    private SpringTemplateEngine getSpringTemplateEngine(PluginRegistryInfo pluginRegistryInfo){
        GenericApplicationContext applicationContext = pluginRegistryInfo.getMainApplicationContext();
        String[] beanNamesForType = applicationContext.getBeanNamesForType(SpringTemplateEngine.class,
                false, false);
        if(beanNamesForType.length == 0){
            return null;
        }
        try {
            return applicationContext.getBean(SpringTemplateEngine.class);
        } catch (Exception e){
            return null;
        }
    }

    private Set<ITemplateResolver> getITemplateResolvers (SpringTemplateEngine springTemplateEngine) throws IllegalAccessException {
        if(springTemplateEngine == null){
            return null;
        }
        return ClassUtils.getReflectionField(springTemplateEngine, "templateResolvers");
    }


}
