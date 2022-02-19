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

package com.gitee.starblues.plugin.pack.utils;

import com.gitee.starblues.plugin.pack.filter.Exclude;
import org.apache.maven.artifact.Artifact;

import java.util.Objects;

/**
 * Object 工具类
 * @author starBlues
 * @version 3.0.0
 */
public class CommonUtils {

    public final static String PLUGIN_FRAMEWORK_GROUP_ID = "com.gitee.starblues";
    public final static String PLUGIN_FRAMEWORK_ARTIFACT_ID = "springboot-plugin-framework";

    public final static String PLUGIN_FRAMEWORK_LOADER_ARTIFACT_ID = "springboot-plugin-framework-loader";

    private CommonUtils(){}

    public static Exclude getPluginFrameworkExclude(){
        return Exclude.get(PLUGIN_FRAMEWORK_GROUP_ID, PLUGIN_FRAMEWORK_ARTIFACT_ID);
    }

    public static boolean isPluginFramework(Artifact artifact){
        return Objects.equals(artifact.getGroupId(), PLUGIN_FRAMEWORK_GROUP_ID)
                && Objects.equals(artifact.getArtifactId(), PLUGIN_FRAMEWORK_ARTIFACT_ID);
    }

    public static boolean isPluginFrameworkLoader(Artifact artifact){
        return Objects.equals(artifact.getGroupId(), PLUGIN_FRAMEWORK_GROUP_ID)
                && Objects.equals(artifact.getArtifactId(), PLUGIN_FRAMEWORK_LOADER_ARTIFACT_ID);
    }
}
