package com.gitee.starblues.core.classloader;

/**
 * @author starBlues
 * @version 1.0
 */
public interface MainResourceMatcher {


    boolean match(String resourceUrl);

    boolean matchSpringFactories(String springFactoriesUrl);
}
