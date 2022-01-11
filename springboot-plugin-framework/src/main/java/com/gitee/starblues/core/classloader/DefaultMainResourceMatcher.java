package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collection;
import java.util.Set;

/**
 * 默认的主程序资源匹配者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultMainResourceMatcher implements MainResourceMatcher{

    private final MainResourcePatternDefiner mainResourcePatternDefiner;
    private final PathMatcher pathMatcher;

    public DefaultMainResourceMatcher(MainResourcePatternDefiner mainResourcePatternDefiner) {
        this.mainResourcePatternDefiner = mainResourcePatternDefiner;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public boolean match(String resourceUrl) {
        Set<String> resourcePatterns = mainResourcePatternDefiner.getIncludePatterns();
        return match(resourcePatterns, resourceUrl);
    }

    private boolean match(Collection<String> patterns, String url){
        if(ObjectUtils.isEmpty(patterns) || ObjectUtils.isEmpty(url)){
            return false;
        }
        url = formatUrl(url);
        for (String pattern : patterns) {
            boolean match = pathMatcher.match(pattern, url);
            if(match){
                return !excludeMatch(mainResourcePatternDefiner.getExcludePatterns(), url);
            }
        }
        return false;
    }

    private boolean excludeMatch(Collection<String> patterns, String url){
        if(ObjectUtils.isEmpty(patterns) || ObjectUtils.isEmpty(url)){
            return false;
        }
        url = formatUrl(url);
        for (String pattern : patterns) {
            boolean match = pathMatcher.match(pattern, url);
            if(match){
                return true;
            }
        }
        return false;
    }


    private String formatUrl(String url){
        url = url.replace("\\", AntPathMatcher.DEFAULT_PATH_SEPARATOR);
        if(url.startsWith(AntPathMatcher.DEFAULT_PATH_SEPARATOR)){
            url = url.substring(url.indexOf(AntPathMatcher.DEFAULT_PATH_SEPARATOR) + 1);
        }
        return url;
    }
}
