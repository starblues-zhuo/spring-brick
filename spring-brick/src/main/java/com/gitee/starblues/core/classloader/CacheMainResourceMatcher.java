/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.core.classloader;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可缓存的 ResourceMatcher
 * @author starBlues
 * @version 3.0.0
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
