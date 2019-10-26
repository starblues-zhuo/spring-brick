package com.gitee.starblues.loader;

import org.springframework.core.io.Resource;

import java.util.*;

/**
 * 资源包装类
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ResourceWrapper {

    private final List<Resource> resources;
    private final Map<String, Object> extensions = new HashMap<>();


    public ResourceWrapper() {
        this.resources = new ArrayList<>(0);
    }

    public ResourceWrapper(List<Resource> resources) {
        if(resources == null){
            this.resources = new ArrayList<>(0);
        } else {
            this.resources = resources;
        }
    }

    public ResourceWrapper(Resource[] resources) {
        if(resources != null){
            this.resources = Arrays.asList(resources);
        } else {
            this.resources = new ArrayList<>(0);
        }
    }

    public List<Resource> getResources(){
        return resources;
    }


    public void addExtension(String key, Object value) {
        extensions.put(key, value);
    }

    public Object getExtension(String key){
        return extensions.get(key);
    }


}
