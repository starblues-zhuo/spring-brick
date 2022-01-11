package com.gitee.starblues.plugin.pack.filter;

import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.AbstractArtifactsFilter;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public abstract class DependencyFilter extends AbstractArtifactsFilter {

    private final List<? extends FilterableDependency> filters;

    public DependencyFilter(List<? extends FilterableDependency> dependencies) {
        this.filters = dependencies;
    }

    @Override
    public Set<Artifact> filter(Set<Artifact> artifacts) throws ArtifactFilterException {
        if(CommonUtils.isEmpty(artifacts)){
            return artifacts;
        }
        Set<Artifact> result = new HashSet<>();
        for (Artifact artifact : artifacts) {
            if (!filter(artifact)) {
                result.add(artifact);
            }
        }
        return result;
    }

    /**
     * 子类过滤结果
     * @param artifact artifact
     * @return boolean
     */
    protected abstract boolean filter(Artifact artifact);

    protected final boolean equals(Artifact artifact, FilterableDependency dependency) {
        if (!dependency.getGroupId().equals(artifact.getGroupId())) {
            return false;
        }
        return dependency.getArtifactId().equals(artifact.getArtifactId());
    }

    protected final List<? extends FilterableDependency> getFilters() {
        return this.filters;
    }

}
