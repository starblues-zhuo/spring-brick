package com.gitee.starblues.plugin.pack.filter;

import org.apache.maven.artifact.Artifact;

import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class IncludeFilter extends DependencyFilter {

    public IncludeFilter(List<Include> includes) {
        super(includes);
    }

    @Override
    protected boolean filter(Artifact artifact) {
        for (FilterableDependency dependency : getFilters()) {
            if (equals(artifact, dependency)) {
                return false;
            }
        }
        return true;
    }

}
