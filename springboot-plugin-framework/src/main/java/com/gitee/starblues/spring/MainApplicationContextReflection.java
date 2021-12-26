package com.gitee.starblues.spring;

import com.gitee.starblues.integration.IntegrationConfiguration;

/**
 * @author starBlues
 * @version 1.0
 */
public class MainApplicationContextReflection extends GenericApplicationContextReflection implements MainApplicationContext {

    private final IntegrationConfiguration configuration;

    public MainApplicationContextReflection(Object mainGenericApplicationContext,
                                            IntegrationConfiguration configuration) {
        super(mainGenericApplicationContext);
        this.configuration = configuration;
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

}
