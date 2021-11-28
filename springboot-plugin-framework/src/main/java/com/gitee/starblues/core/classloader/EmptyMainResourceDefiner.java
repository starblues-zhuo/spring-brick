package com.gitee.starblues.core.classloader;

import java.util.Set;

/**
 * 空的 MainResourceDefiner
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyMainResourceDefiner implements MainResourceDefiner{
    @Override
    public Set<String> getClassNames() {
        return null;
    }

    @Override
    public Set<String> getResources() {
        return null;
    }

    @Override
    public Set<String> getSpringFactories() {
        return null;
    }
}
