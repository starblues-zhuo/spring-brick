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
 * @version 3.0.0
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
        // 添加spring web 环境排除
        addSpringWebEnvExclude();
        filters.addFilter(new ExcludeFilter(this.excludes));
        return filters;
    }

    private void addPluginFrameworkExclude(){
        excludes.add(Exclude.get("com.gitee.starblues", "springboot-plugin-framework"));
    }

    private void addSpringWebEnvExclude(){
        excludes.add(Exclude.get("org.springframework.boot", "spring-boot-starter-web"));
        excludes.add(Exclude.get("org.springframework.boot", "spring-boot-starter-tomcat"));
        excludes.add(Exclude.get("org.springframework.boot", "spring-boot-starter-json"));
        excludes.add(Exclude.get("org.springframework", "spring-webmvc"));
        excludes.add(Exclude.get("org.springframework", "spring-web"));
    }

}
