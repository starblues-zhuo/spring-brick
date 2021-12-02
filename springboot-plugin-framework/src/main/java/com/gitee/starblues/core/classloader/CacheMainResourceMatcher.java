package com.gitee.starblues.core.classloader;

import com.gitee.starblues.core.ResourceClear;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
 */
public class CacheMainResourceMatcher extends DefaultMainResourceMatcher implements ResourceClear {

    private final Map<String, Boolean> resourceUrlMatchCache = new ConcurrentHashMap<>();
    private final Map<String, Boolean> springFactoriesUrlMatchCache = new ConcurrentHashMap<>();

    public CacheMainResourceMatcher(MainResourcePatternDefiner mainResourcePatternDefiner) {
        super(mainResourcePatternDefiner);
    }

    @Override
    public boolean match(String resourceUrl) {
        Boolean match = resourceUrlMatchCache.get(resourceUrl);
        if(match == null){
            match = super.match(resourceUrl);
            resourceUrlMatchCache.put(resourceUrl, match);
        }
        return match;
    }

    @Override
    public boolean matchSpringFactories(String springFactoriesUrl) {
        Boolean match = springFactoriesUrlMatchCache.get(springFactoriesUrl);
        if(match == null){
            match = super.matchSpringFactories(springFactoriesUrl);
            springFactoriesUrlMatchCache.put(springFactoriesUrl, match);
        }
        return match;
    }

    @Override
    public void clear(){
        resourceUrlMatchCache.clear();
        springFactoriesUrlMatchCache.clear();
    }

}
