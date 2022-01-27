package com.gitee.starblues.plugin.pack.dev;

import com.gitee.starblues.plugin.pack.BasicRepackager;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import lombok.Getter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.*;

/**
 * @author starBlues
 * @version 1.0
 */
public class DevRepackager extends BasicRepackager {

    @Getter
    private Map<String, Dependency> moduleDependencies = Collections.emptyMap();

    public DevRepackager(RepackageMojo repackageMojo) {
        super(repackageMojo);
    }

    @Override
    protected Set<String> getDependenciesIndexSet() throws Exception {
        moduleDependencies = getModuleDependencies(repackageMojo.getDevConfig());
        Set<String> dependenciesIndexSet = super.getDependenciesIndexSet();
        for (Dependency dependency : moduleDependencies.values()) {
            dependenciesIndexSet.add(dependency.getClassesPath());
        }
        return dependenciesIndexSet;
    }

    @Override
    protected boolean filterArtifact(Artifact artifact) {
        if(super.filterArtifact(artifact)){
            return true;
        }
        String moduleDependencyKey = getModuleDependencyKey(artifact.getGroupId(), artifact.getArtifactId());
        Dependency dependency = moduleDependencies.get(moduleDependencyKey);
        return dependency != null && !CommonUtils.isEmpty(dependency.getClassesPath());
    }

    protected Map<String, Dependency> getModuleDependencies(DevConfig devConfig) {
        if(devConfig == null){
            return Collections.emptyMap();
        }
        List<Dependency> moduleDependencies = devConfig.getModuleDependencies();
        if(CommonUtils.isEmpty(moduleDependencies)){
            return Collections.emptyMap();
        }
        Map<String, Dependency> moduleDependenciesMap = new HashMap<>();
        for (Dependency dependency : moduleDependencies) {
            String moduleDependencyKey = getModuleDependencyKey(dependency.getGroupId(),
                    dependency.getArtifactId());
            if(moduleDependencyKey == null){
                continue;
            }
            moduleDependenciesMap.put(moduleDependencyKey, dependency);
        }
        return moduleDependenciesMap;
    }

    protected String getModuleDependencyKey(String groupId, String artifactId){
        if(CommonUtils.isEmpty(groupId) || CommonUtils.isEmpty(artifactId)){
            return null;
        }
        return groupId + artifactId;
    }

}
