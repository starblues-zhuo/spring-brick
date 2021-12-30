package com.gitee.starblues.spring.invoke;

import com.gitee.starblues.spring.ApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class SupperCache {

    private final String supperKey;
    private final String beanName;
    private final ApplicationContext applicationContext;

    public SupperCache(String supperKey, String beanName, ApplicationContext applicationContext) {
        this.supperKey = supperKey;
        this.beanName = beanName;
        this.applicationContext = applicationContext;
    }

    public String getSupperKey() {
        return supperKey;
    }

    public String getBeanName() {
        return beanName;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
