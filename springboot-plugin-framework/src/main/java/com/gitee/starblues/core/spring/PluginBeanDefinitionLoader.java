package com.gitee.starblues.core.spring;

import com.gitee.starblues.utils.Assert;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

/**
 * 加载插件中的bean
 * @author starBlues
 * @version 3.0.0
 */
public class PluginBeanDefinitionLoader {
    private static final boolean XML_ENABLED = !SpringProperties.getFlag("spring.xml.ignore");

    private final Object[] sources;

    private final AnnotatedBeanDefinitionReader annotatedReader;

    private final AbstractBeanDefinitionReader xmlReader;

    private final BeanDefinitionReader groovyReader;

    private final ClassPathBeanDefinitionScanner scanner;

    private ResourceLoader resourceLoader;

    PluginBeanDefinitionLoader(BeanDefinitionRegistry registry, Object... sources) {
        Assert.isNotNull(registry, "参数 registry 不能为空");
        Assert.isNotEmpty(sources, "参数 sources 不能为空");
        this.sources = sources;
        this.annotatedReader = new AnnotatedBeanDefinitionReader(registry);
        this.xmlReader = (XML_ENABLED ? new XmlBeanDefinitionReader(registry) : null);
        this.groovyReader = (isGroovyPresent() ? new GroovyBeanDefinitionReader(registry) : null);
        this.scanner = new ClassPathBeanDefinitionScanner(registry);
        this.scanner.addExcludeFilter(new PluginBeanDefinitionLoader.ClassExcludeFilter(sources));
    }

    void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.annotatedReader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        if (this.xmlReader != null) {
            this.xmlReader.setBeanNameGenerator(beanNameGenerator);
        }
    }


    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.scanner.setResourceLoader(resourceLoader);
        if (this.xmlReader != null) {
            this.xmlReader.setResourceLoader(resourceLoader);
        }
    }


    void setEnvironment(ConfigurableEnvironment environment) {
        this.annotatedReader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
        if (this.xmlReader != null) {
            this.xmlReader.setEnvironment(environment);
        }
    }

    void load() {
        for (Object source : this.sources) {
            load(source);
        }
    }

    private void load(Object source) {
        Assert.isNotEmpty(source, "Source must not be null");
        if (source instanceof Class<?>) {
            load((Class<?>) source);
            return;
        }
        if (source instanceof Resource) {
            load((Resource) source);
            return;
        }
        if (source instanceof Package) {
            load((Package) source);
            return;
        }
        if (source instanceof CharSequence) {
            load((CharSequence) source);
            return;
        }
        throw new IllegalArgumentException("Invalid source type " + source.getClass());
    }

    private void load(Class<?> source) {
        if (isEligible(source)) {
            this.annotatedReader.register(source);
        }
    }

    private void load(Resource source) {
        if (source.getFilename().endsWith(".groovy")) {
            if (this.groovyReader == null) {
                throw new BeanDefinitionStoreException("Cannot load Groovy beans without Groovy on classpath");
            }
            this.groovyReader.loadBeanDefinitions(source);
        }
        else {
            if (this.xmlReader == null) {
                throw new BeanDefinitionStoreException("Cannot load XML bean definitions when XML support is disabled");
            }
            this.xmlReader.loadBeanDefinitions(source);
        }
    }

    private void load(Package source) {
        this.scanner.scan(source.getName());
    }

    private void load(CharSequence source) {
        String resolvedSource = this.scanner.getEnvironment().resolvePlaceholders(source.toString());
        // Attempt as a Class
        try {
            load(ClassUtils.forName(resolvedSource, null));
            return;
        }
        catch (IllegalArgumentException | ClassNotFoundException ex) {
            // swallow exception and continue
        }
        // Attempt as Resources
        if (loadAsResources(resolvedSource)) {
            return;
        }
        // Attempt as package
        Package packageResource = findPackage(resolvedSource);
        if (packageResource != null) {
            load(packageResource);
            return;
        }
        throw new IllegalArgumentException("Invalid source '" + resolvedSource + "'");
    }

    private boolean loadAsResources(String resolvedSource) {
        boolean foundCandidate = false;
        Resource[] resources = findResources(resolvedSource);
        for (Resource resource : resources) {
            if (isLoadCandidate(resource)) {
                foundCandidate = true;
                load(resource);
            }
        }
        return foundCandidate;
    }

    private boolean isGroovyPresent() {
        return ClassUtils.isPresent("groovy.lang.MetaClass", null);
    }

    private Resource[] findResources(String source) {
        ResourceLoader loader = (this.resourceLoader != null) ? this.resourceLoader
                : new PathMatchingResourcePatternResolver();
        try {
            if (loader instanceof ResourcePatternResolver) {
                return ((ResourcePatternResolver) loader).getResources(source);
            }
            return new Resource[] { loader.getResource(source) };
        }
        catch (IOException ex) {
            throw new IllegalStateException("Error reading source '" + source + "'");
        }
    }

    private boolean isLoadCandidate(Resource resource) {
        if (resource == null || !resource.exists()) {
            return false;
        }
        if (resource instanceof ClassPathResource) {
            // A simple package without a '.' may accidentally get loaded as an XML
            // document if we're not careful. The result of getInputStream() will be
            // a file list of the package content. We double check here that it's not
            // actually a package.
            String path = ((ClassPathResource) resource).getPath();
            if (path.indexOf('.') == -1) {
                try {
                    return Package.getPackage(path) == null;
                }
                catch (Exception ex) {
                    // Ignore
                }
            }
        }
        return true;
    }

    private Package findPackage(CharSequence source) {
        Package pkg = Package.getPackage(source.toString());
        if (pkg != null) {
            return pkg;
        }
        try {
            // Attempt to find a class in this package
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
            Resource[] resources = resolver
                    .getResources(ClassUtils.convertClassNameToResourcePath(source.toString()) + "/*.class");
            for (Resource resource : resources) {
                String className = StringUtils.stripFilenameExtension(resource.getFilename());
                load(Class.forName(source.toString() + "." + className));
                break;
            }
        }
        catch (Exception ex) {
            // swallow exception and continue
        }
        return Package.getPackage(source.toString());
    }

    /**
     * Check whether the bean is eligible for registration.
     * @param type candidate bean type
     * @return true if the given bean type is eligible for registration, i.e. not a groovy
     * closure nor an anonymous class
     */
    private boolean isEligible(Class<?> type) {
        return !(type.isAnonymousClass() || isGroovyClosure(type) || hasNoConstructors(type));
    }

    private boolean isGroovyClosure(Class<?> type) {
        return type.getName().matches(".*\\$_.*closure.*");
    }

    private boolean hasNoConstructors(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        return ObjectUtils.isEmpty(constructors);
    }

    /**
     * Simple {@link TypeFilter} used to ensure that specified {@link Class} sources are
     * not accidentally re-added during scanning.
     */
    private static class ClassExcludeFilter extends AbstractTypeHierarchyTraversingFilter {

        private final Set<String> classNames = new HashSet<>();

        ClassExcludeFilter(Object... sources) {
            super(false, false);
            for (Object source : sources) {
                if (source instanceof Class<?>) {
                    this.classNames.add(((Class<?>) source).getName());
                }
            }
        }

        @Override
        protected boolean matchClassName(String className) {
            return this.classNames.contains(className);
        }

    }


}
