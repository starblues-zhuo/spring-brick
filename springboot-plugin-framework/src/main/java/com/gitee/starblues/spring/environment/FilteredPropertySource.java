package com.gitee.starblues.spring.environment;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Set;
import java.util.function.Consumer;

/**
 * PropertySource 过滤
 * @author starBlues
 * @version 3.0.0
 */
public class FilteredPropertySource extends PropertySource<PropertySource<?>> {

    private final Set<String> filteredProperties;

    FilteredPropertySource(PropertySource<?> original, Set<String> filteredProperties) {
        super(original.getName(), original);
        this.filteredProperties = filteredProperties;
    }

    @Override
    public Object getProperty(String name) {
        if (this.filteredProperties.contains(name)) {
            return null;
        }
        return getSource().getProperty(name);
    }

    static void apply(ConfigurableEnvironment environment, String propertySourceName, Set<String> filteredProperties,
                      Consumer<PropertySource<?>> operation) {
        MutablePropertySources propertySources = environment.getPropertySources();
        PropertySource<?> original = propertySources.get(propertySourceName);
        if (original == null) {
            operation.accept(null);
            return;
        }
        propertySources.replace(propertySourceName, new FilteredPropertySource(original, filteredProperties));
        try {
            operation.accept(original);
        }
        finally {
            propertySources.replace(propertySourceName, original);
        }
    }

}

