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

/**
 * 排除的依赖定义
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
