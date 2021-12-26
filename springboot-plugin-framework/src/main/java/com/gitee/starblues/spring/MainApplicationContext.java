package com.gitee.starblues.spring;

import com.gitee.starblues.integration.IntegrationConfiguration;

/**
 * @author starBlues
 * @version 1.0
 */
public interface MainApplicationContext extends ApplicationContext {

    IntegrationConfiguration getConfiguration();

}
