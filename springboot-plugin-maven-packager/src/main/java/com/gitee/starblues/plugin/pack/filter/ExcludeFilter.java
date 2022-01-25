package com.gitee.starblues.plugin.pack.filter;

import org.apache.maven.artifact.Artifact;

import java.util.Arrays;
import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class ExcludeFilter extends DependencyFilter {

    public ExcludeFilter(Exclude... excludes) {
        this(Arrays.asList(excludes));
    }

    public ExcludeFilter(List<Exclude> excludes) {
        super(excludes);
    }

    @Override
    protected boolean filter(Artifact artifact) {
        for (FilterableDependency dependency : getFilters()) {
            if (equals(artifact, dependency)) {
                return true;
            }
        }
        return false;
    }

}
