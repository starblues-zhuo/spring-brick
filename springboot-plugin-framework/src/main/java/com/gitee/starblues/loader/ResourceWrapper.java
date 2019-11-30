package com.gitee.starblues.loader;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 资源包装类
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class ResourceWrapper {

    private final List<Resource> resources = new ArrayList<>();
    private final Set<String> classPackageNames = new HashSet<>();
    private final Map<String, Object> extensions = new HashMap<>();

    public void addResource(Resource resource){
        if(resource == null){
            return;
        }
        resources.add(resource);
    }

    public void addResources(List<Resource> resources){
        if(resources == null || resources.isEmpty()){
            return;
        }
        this.resources.addAll(resources);
    }

    public List<Resource> getResources(){
        return Collections.unmodifiableList(resources);
    }

    public void addClassPackageName(String classFullName){
        if(StringUtils.isEmpty(classFullName)){
            return;
        }
        classPackageNames.add(classFullName);
    }

    public void addClassPackageNames(Set<String> classPackageNames){
        if(classPackageNames == null || classPackageNames.isEmpty()){
            return;
        }
        this.classPackageNames.addAll(classPackageNames);
    }

    public Set<String> getClassPackageNames(){
        return Collections.unmodifiableSet(classPackageNames);
    }


    public void addExtension(String key, Object value) {
        extensions.put(key, value);
    }

    public Object getExtension(String key){
        return extensions.get(key);
    }


}
