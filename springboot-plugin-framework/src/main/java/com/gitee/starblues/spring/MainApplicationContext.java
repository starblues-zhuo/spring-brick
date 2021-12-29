package com.gitee.starblues.spring;

import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public interface MainApplicationContext extends ApplicationContext {


    Object resolveDependency(Object descriptor, String requestingBeanName);


    Object resolveDependency(Object descriptor, String requestingBeanName,
                             Set<String> autowiredBeanNames, Object typeConverter);

}
