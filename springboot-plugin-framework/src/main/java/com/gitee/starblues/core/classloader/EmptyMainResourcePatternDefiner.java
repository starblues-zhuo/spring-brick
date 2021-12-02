package com.gitee.starblues.core.classloader;

import java.util.Set;

/**
 * 空的 MainResourceDefiner
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyMainResourcePatternDefiner implements MainResourcePatternDefiner {

    @Override
    public Set<String> getResourcePatterns() {
        return null;
    }

    @Override
    public Set<String> getSpringFactoriesPatterns() {
        return null;
    }
}
