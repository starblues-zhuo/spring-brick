package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;
import com.gitee.starblues.core.launcher.JavaMainResourcePatternDefiner;

import java.util.HashSet;
import java.util.Set;

/**
 * 基本的主程序资源匹配定义
 * @author starBlues
 * @version 3.0.0
 */
public class BasicMainResourcePatternDefiner implements MainResourcePatternDefiner {

    private final String mainPackageName;

    public BasicMainResourcePatternDefiner(String mainPackageName) {
        this.mainPackageName = mainPackageName;
    }

    @Override
    public Set<String> getIncludePatterns() {
        Set<String> includePatterns = new HashSet<>();
        includePatterns.add(mainPackageName);
        return includePatterns;
    }

    @Override
    public Set<String> getExcludePatterns() {
        return null;
    }
}
