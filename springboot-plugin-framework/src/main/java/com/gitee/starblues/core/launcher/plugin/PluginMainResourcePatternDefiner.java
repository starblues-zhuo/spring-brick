package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.launcher.JavaMainResourcePatternDefiner;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.Set;

/**
 * 定义插件从主程序加载资源的匹配
 * @author starBlues
 * @version 3.0.0
 */
public class PluginMainResourcePatternDefiner extends JavaMainResourcePatternDefiner {

    private static final String FRAMEWORK = "com/gitee/starblues/**";
    private static final String SPRING_WEB = "org/springframework/web/**";

    private final InsidePluginDescriptor descriptor;

    public PluginMainResourcePatternDefiner(InsidePluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public Set<String> getIncludePatterns() {
        Set<String> includeResourcePatterns = super.getIncludePatterns();
        includeResourcePatterns.add(FRAMEWORK);
        includeResourcePatterns.add(SPRING_WEB);

        // 配置插件自定义从主程序加载的资源匹配
        Set<String> includeMainResourcePatterns = descriptor.getIncludeMainResourcePatterns();
        if(ObjectUtils.isEmpty(includeMainResourcePatterns)){
            return includeResourcePatterns;
        }

        for (String includeMainResourcePattern : includeMainResourcePatterns) {
            if(ObjectUtils.isEmpty(includeMainResourcePattern)){
                continue;
            }
            includeResourcePatterns.add(includeMainResourcePattern);
        }
        return includeResourcePatterns;
    }

    @Override
    public Set<String> getExcludePatterns() {
        return descriptor.getExcludeMainResourcePatterns();
    }
}
