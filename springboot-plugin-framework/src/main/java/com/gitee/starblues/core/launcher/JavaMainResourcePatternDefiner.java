package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;

import java.util.HashSet;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class JavaMainResourcePatternDefiner implements MainResourcePatternDefiner {

    private final Set<String> includes = new HashSet<>();

    public JavaMainResourcePatternDefiner(){
        // == java ==
        includes.add("java/**");
        includes.add("javax/**");
        includes.add("sun/**");
        includes.add("org/xml/**");
        includes.add("jdk/**");
        includes.add("org/w3c/**");
    }

    @Override
    public Set<String> getIncludeResourcePatterns() {
        return includes;
    }

    @Override
    public Set<String> getExcludeResourcePatterns() {
        return null;
    }

    @Override
    public Set<String> getSpringFactoriesPatterns() {
        return null;
    }
}
