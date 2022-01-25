package com.gitee.starblues.plugin.pack.filter;

/**
 * 排除依赖定义
 * @author starBlues
 * @version 3.0.0
 */
public class Exclude extends FilterableDependency{

    public static Exclude get(String groupId, String artifactId){
        Exclude exclude = new Exclude();
        exclude.setGroupId(groupId);
        exclude.setArtifactId(artifactId);
        return exclude;
    }


}
