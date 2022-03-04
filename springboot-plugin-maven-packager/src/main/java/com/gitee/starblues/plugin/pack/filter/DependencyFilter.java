/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.plugin.pack.filter;

import com.gitee.starblues.utils.ObjectUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.AbstractArtifactsFilter;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 依赖过滤
 * @author starBlues
 * @version 3.0.0
 */
public abstract class DependencyFilter extends AbstractArtifactsFilter {

    private final List<? extends FilterableDependency> filters;

    public DependencyFilter(List<? extends FilterableDependency> dependencies) {
        this.filters = dependencies;
    }

    @Override
    public Set<Artifact> filter(Set<Artifact> artifacts) throws ArtifactFilterException {
        if(ObjectUtils.isEmpty(artifacts)){
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
