/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.classloader.resource.loader.DefaultResourceLoaderFactory;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.loader.classloader.resource.storage.CacheResourceStorage;
import com.gitee.starblues.loader.classloader.resource.storage.DefaultResourceStorage;
import com.gitee.starblues.loader.classloader.resource.storage.ResourceStorage;
import com.gitee.starblues.loader.classloader.resource.storage.ShareResourceStorage;

import java.util.Objects;

/**
 * 获取ResourceLoaderFactory
 *
 * @author starBlues
 * @version 3.0.0
 */
public class ResourceLoaderFactoryGetter {

    private static final String PARAMS_KEY = "--resource.store.mode";


    /**
     * 资源模式--缓存隔离模式
     */
    private static final String RESOURCE_MODE_CACHE_ISOLATION = "cache-isolation";


    /**
     * 资源模式--缓存共享模式
     */
    private static final String RESOURCE_MODE_CACHE_SHARE = "cache-share";


    /**
     * 资源模式--不缓存模式
     */
    private static final String RESOURCE_MODE_NO_CACHE = "no-cache";

    private static volatile String resourceMode;


    public static ResourceLoaderFactory create(String classLoaderName, String... args){
        if(resourceMode == null){
            synchronized (ResourceLoaderFactory.class){
                if(resourceMode == null){
                    resourceMode = parseArg(args);
                }
            }
        }
        return instant(classLoaderName);
    }

    public static synchronized ResourceLoaderFactory get(String classLoaderName){
        return instant(classLoaderName);
    }

    private static String parseArg(String... args){
        for (String arg : args) {
            if(arg.startsWith(PARAMS_KEY)){
                String[] split = arg.split("=");
                if(split.length != 2){
                    return null;
                }
                return split[1];
            }
        }
        return null;
    }

    public static ResourceStorage getResourceStorage(String key){
        ResourceStorage resourceStorage = null;
        if(Objects.equals(resourceMode, RESOURCE_MODE_NO_CACHE)){
            resourceStorage = new DefaultResourceStorage();
        } else if(Objects.equals(resourceMode, RESOURCE_MODE_CACHE_SHARE)){
            resourceStorage = new ShareResourceStorage(key);
        } else {
            resourceStorage = new CacheResourceStorage();
        }
        return resourceStorage;
    }

    public static ResourceStorage getMainResourceStorage(){
        return getResourceStorage(MainProgramLauncher.MAIN_CLASS_LOADER_NAME);
    }

    private static ResourceLoaderFactory instant(String classLoaderName){
        return new DefaultResourceLoaderFactory(classLoaderName);
    }

}
