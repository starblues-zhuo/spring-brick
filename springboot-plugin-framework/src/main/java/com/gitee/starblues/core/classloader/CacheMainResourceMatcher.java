package com.gitee.starblues.core.classloader;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author starBlues
 * @version 1.0
 */
public class CacheMainResourceMatcher extends DefaultMainResourceMatcher implements AutoCloseable {

    private final Map<String, Boolean> resourceUrlMatchCache = new ConcurrentHashMap<>();

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
    public void close() throws Exception {
        resourceUrlMatchCache.clear();
    }
}
