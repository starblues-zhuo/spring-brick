package com.gitee.starblues.extension.cloud.openfeign;

import com.gitee.starblues.extension.cloud.openfeign.nacos.RibbonNacosAutoConfiguration;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.realize.BasePlugin;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.encoding.FeignAcceptGzipEncodingAutoConfiguration;
import org.springframework.cloud.openfeign.encoding.FeignContentGzipEncodingAutoConfiguration;
import org.springframework.cloud.openfeign.hateoas.FeignHalAutoConfiguration;
import org.springframework.cloud.openfeign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * OpenFeign 注册者
 * @author starBlues
 * @version 2.4.6
 */
public class OpenFeignRegistrar implements PluginBeanRegistrarExtend {

    @Override
    public String key() {
        return "OpenFeignRegistrar";
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        // 先注册 feignClient bean
        boolean isRegistry = registryFeignClientBean(pluginRegistryInfo);
        if(!isRegistry){
            // 如果发现 FeignClient 则, 不进行注册 openFeign
            return;
        }
        installAutoConfiguration(pluginRegistryInfo);
    }

    private void installAutoConfiguration(PluginRegistryInfo pluginRegistryInfo) {
        List<? extends BasePlugin> basePlugins = SpringFactoriesLoader
                .loadFactories(pluginRegistryInfo.getBasePlugin().getClass(), pluginRegistryInfo.getPluginClassLoader());


        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        pluginApplicationContext.registerBean(FeignRibbonClientAutoConfiguration.class);
        pluginApplicationContext.registerBean(FeignHalAutoConfiguration.class);
        pluginApplicationContext.registerBean(FeignAutoConfiguration.class);
        pluginApplicationContext.registerBean(FeignAcceptGzipEncodingAutoConfiguration.class);
        pluginApplicationContext.registerBean(FeignContentGzipEncodingAutoConfiguration.class);
        //pluginApplicationContext.registerBean(FeignLoadBalancerAutoConfiguration.class);
        pluginApplicationContext.registerBean(RibbonNacosAutoConfiguration.class);
        pluginApplicationContext.registerBean(RibbonAutoConfiguration.class);
        pluginApplicationContext.registerBean(HttpMessageConvertersAutoConfiguration.class);
    }

    private boolean registryFeignClientBean(PluginRegistryInfo pluginRegistryInfo) {
        List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(FeignClientBeanGroup.GROUP_ID);
        if(ObjectUtils.isEmpty(groupClasses)){
            return false;
        }
        GenericApplicationContext registry = pluginRegistryInfo.getPluginApplicationContext();
        for (Class<?> groupClass : groupClasses) {
            AnnotatedGenericBeanDefinition beanDefinition = new AnnotatedGenericBeanDefinition(groupClass);
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            Assert.isTrue(annotationMetadata.isInterface(),
                    "@FeignClient can only be specified on an interface");

            Map<String, Object> attributes = annotationMetadata
                    .getAnnotationAttributes(FeignClient.class.getCanonicalName());
            if(attributes == null){
                continue;
            }
            String name = getClientName(attributes);
            registerClientConfiguration(registry, name, attributes.get("configuration"));

            BeanDefinitionHolder beanDefinitionHolder = getBeanDefinition(registry, annotationMetadata, attributes);
            pluginRegistryInfo.getSpringBeanRegister().registerBeanDefinition(beanDefinitionHolder);
        }
        return true;
    }

    private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name,
                                             Object configuration) {
        if(configuration == null){
            return;
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(FeignClientSpecification.class);
        builder.addConstructorArgValue(name);
        builder.addConstructorArgValue(configuration);
        registry.registerBeanDefinition(
                name + "." + FeignClientSpecification.class.getSimpleName(),
                builder.getBeanDefinition());
    }

    private BeanDefinitionHolder getBeanDefinition(GenericApplicationContext applicationContext,
                                                   AnnotationMetadata annotationMetadata,
                                                   Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        Class clazz = ClassUtils.resolveClassName(className, applicationContext.getClassLoader());
        String contextId = getContextId(applicationContext, attributes);
        String name = getName(applicationContext, attributes);
        FeignClientFactoryBean factoryBean = new FeignClientFactoryBean();
        factoryBean.setBeanFactory(applicationContext);
        factoryBean.setName(name);
        factoryBean.setContextId(contextId);
        factoryBean.setType(clazz);
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(clazz, () -> {
                    factoryBean.setUrl(getUrl(applicationContext, attributes));
                    factoryBean.setPath(getPath(applicationContext, attributes));
                    factoryBean.setDecode404(Boolean
                            .parseBoolean(String.valueOf(attributes.get("decode404"))));
                    Object fallback = attributes.get("fallback");
                    if (fallback != null) {
                        factoryBean.setFallback(fallback instanceof Class
                                ? (Class<?>) fallback
                                : ClassUtils.resolveClassName(fallback.toString(), null));
                    }
                    Object fallbackFactory = attributes.get("fallbackFactory");
                    if (fallbackFactory != null) {
                        factoryBean.setFallbackFactory(fallbackFactory instanceof Class
                                ? (Class<?>) fallbackFactory
                                : ClassUtils.resolveClassName(fallbackFactory.toString(),
                                null));
                    }
                    return factoryBean.getObject();
                });
        definition.setAutowireMode(2);
        definition.setLazyInit(true);
        validate(attributes);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute("factoryBeanObjectType", className);
        beanDefinition.setAttribute("feignClientsRegistrarFactoryBean", factoryBean);

        // has a default, won't be null
        boolean primary = (Boolean) attributes.get("primary");

        beanDefinition.setPrimary(primary);

        String[] qualifiers = getQualifiers(attributes);
        if (ObjectUtils.isEmpty(qualifiers)) {
            qualifiers = new String[] { contextId + "FeignClient" };
        }

        return new BeanDefinitionHolder(beanDefinition, className, qualifiers);
    }

    private void validate(Map<String, Object> attributes) {
        AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
        // This blows up if an aliased property is overspecified
        // FIXME annotation.getAliasedString("name", FeignClient.class, null);
        validateFallback(annotation.getClass("fallback"));
        validateFallbackFactory(annotation.getClass("fallbackFactory"));
    }

    private void validateFallback(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(),
                "Fallback class must implement the interface annotated by @FeignClient");
    }

    private void validateFallbackFactory(final Class clazz) {
        Assert.isTrue(!clazz.isInterface(), "Fallback factory must produce instances "
                + "of fallback classes that implement the interface annotated by @FeignClient");
    }

    private String getContextId(GenericApplicationContext applicationContext,
                                Map<String, Object> attributes) {
        String contextId = (String) attributes.get("contextId");
        if (!StringUtils.hasText(contextId)) {
            return getName(applicationContext, attributes);
        }

        contextId = resolve(applicationContext, contextId);
        return getName(contextId);
    }

    private String getQualifier(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String qualifier = (String) client.get("qualifier");
        if (StringUtils.hasText(qualifier)) {
            return qualifier;
        }
        return null;
    }

    private String[] getQualifiers(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        List<String> qualifierList = new ArrayList<>(
                Arrays.asList((String[]) client.get("qualifiers")));
        qualifierList.removeIf(qualifier -> !StringUtils.hasText(qualifier));
        if (qualifierList.isEmpty() && getQualifier(client) != null) {
            qualifierList = Collections.singletonList(getQualifier(client));
        }
        return !qualifierList.isEmpty() ? qualifierList.toArray(new String[0]) : null;
    }

    private String getName(GenericApplicationContext applicationContext,
                           Map<String, Object> attributes) {
        String name = (String) attributes.get("serviceId");
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("name");
        }
        if (!StringUtils.hasText(name)) {
            name = (String) attributes.get("value");
        }
        name = resolve(applicationContext, name);
        return getName(name);
    }

    private String getName(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }

        String host = null;
        try {
            String url;
            if (!name.startsWith("http://") && !name.startsWith("https://")) {
                url = "http://" + name;
            }
            else {
                url = name;
            }
            host = new URI(url).getHost();

        } catch (URISyntaxException e) {
            // 忽略
        }
        Assert.state(host != null, "Service id not legal hostname (" + name + ")");
        return name;
    }

    private String getUrl(GenericApplicationContext applicationContext,
                          Map<String, Object> attributes) {
        String url = resolve(applicationContext, (String) attributes.get("url"));
        return getUrl(url);
    }

    private String getUrl(String url) {
        if (StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
            if (!url.contains("://")) {
                url = "http://" + url;
            }
            try {
                new URL(url);
            }
            catch (MalformedURLException e) {
                throw new IllegalArgumentException(url + " is malformed", e);
            }
        }
        return url;
    }


    private String getPath(GenericApplicationContext applicationContext,
                           Map<String, Object> attributes) {
        String path = resolve(applicationContext, (String) attributes.get("path"));
        return getPath(path);
    }


    private String getPath(String path) {
        if (StringUtils.hasText(path)) {
            path = path.trim();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }
        return path;
    }

    private String getClientName(Map<String, Object> client) {
        if (client == null) {
            return null;
        }
        String value = (String) client.get("contextId");
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("value");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("name");
        }
        if (!StringUtils.hasText(value)) {
            value = (String) client.get("serviceId");
        }
        if (StringUtils.hasText(value)) {
            return value;
        }

        throw new IllegalStateException("Either 'name' or 'value' must be provided in @"
                + FeignClient.class.getSimpleName());
    }

    private String resolve(GenericApplicationContext applicationContext, String value) {
        if (StringUtils.hasText(value)) {
            return applicationContext.getEnvironment().resolvePlaceholders(value);
        }
        return value;
    }

}
