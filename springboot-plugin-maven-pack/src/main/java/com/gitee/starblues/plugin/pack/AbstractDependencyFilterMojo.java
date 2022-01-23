package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.plugin.pack.filter.Exclude;
import com.gitee.starblues.plugin.pack.filter.ExcludeFilter;
import com.gitee.starblues.plugin.pack.filter.Include;
import com.gitee.starblues.plugin.pack.filter.IncludeFilter;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AbstractDependencyFilterMojo extends AbstractMojo {


    @Parameter(property = "springboot-plugin.includes")
    private List<Include> includes;

    @Parameter(property = "springboot-plugin.excludes")
    private List<Exclude> excludes;


    protected final Set<Artifact> filterDependencies(Set<Artifact> dependencies, FilterArtifacts filters)
            throws MojoExecutionException {
        try {
            Set<Artifact> filtered = new LinkedHashSet<>(dependencies);
            filtered.retainAll(filters.filter(dependencies));
            return filtered;
        }
        catch (ArtifactFilterException ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    protected final FilterArtifacts getFilters(ArtifactsFilter... additionalFilters) {
        FilterArtifacts filters = new FilterArtifacts();
        for (ArtifactsFilter additionalFilter : additionalFilters) {
            filters.addFilter(additionalFilter);
        }
        if (!CommonUtils.isEmpty(includes)) {
            filters.addFilter(new IncludeFilter(this.includes));
        }
        if(CommonUtils.isEmpty(excludes)){
            excludes = new ArrayList<>();
        }
        // 添加主框架排除
        addPluginFrameworkExclude();
        filters.addFilter(new ExcludeFilter(this.excludes));
        return filters;
    }

    private void addPluginFrameworkExclude(){
        final Exclude pluginFrameworkExclude = new Exclude();
        pluginFrameworkExclude.setGroupId("com.gitee.starblues");
        pluginFrameworkExclude.setArtifactId("springboot-plugin-framework");
        excludes.add(pluginFrameworkExclude);
    }

}
