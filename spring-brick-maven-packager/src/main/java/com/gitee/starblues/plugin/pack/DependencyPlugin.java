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

package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.common.AbstractDependencyPlugin;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 依赖的插件
 * @author starBlues
 * @version 3.0.0
 */
public class DependencyPlugin extends AbstractDependencyPlugin {

    @Parameter(required = true)
    private String id;

    @Parameter(required = true)
    private String version;

    @Parameter(required = false, defaultValue = "true")
    private Boolean optional = false;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Boolean getOptional() {
        if(optional == null){
            return false;
        }
        return optional;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

}
