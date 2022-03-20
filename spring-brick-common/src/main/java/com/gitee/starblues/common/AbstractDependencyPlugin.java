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

package com.gitee.starblues.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 依赖的插件
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractDependencyPlugin implements DependencyPlugin{

    public static final String SPLIT_ALL = ",";
    public static final String SPLIT_ONE = "@";


    /**
     * set依赖插件id
     *
     * @param id 插件id
     */
    public abstract void setId(String id);

    /**
     * set依赖插件版本
     *
     * @param version 插件版本
     */
    public abstract void setVersion(String version);

    /**
     * set optional
     *
     * @param optional 是否可选
     */
    public abstract void setOptional(Boolean optional);


    public static String toStr(List<? extends AbstractDependencyPlugin> dependencyPlugins){
        if(dependencyPlugins == null || dependencyPlugins.isEmpty()){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        final int size = dependencyPlugins.size();
        for (int i = 0; i < size; i++) {
            AbstractDependencyPlugin dependencyPlugin = dependencyPlugins.get(i);
            Boolean optional = dependencyPlugin.getOptional();
            if(optional == null){
                optional = false;
            }
            stringBuilder.append(dependencyPlugin.getId())
                    .append(SPLIT_ONE).append(dependencyPlugin.getVersion())
                    .append(SPLIT_ONE).append(optional);
            if(i <= size - 2){
                stringBuilder.append(SPLIT_ALL);
            }
        }
        return stringBuilder.toString();
    }


    public static List<DependencyPlugin> toList(String str, Supplier<? extends AbstractDependencyPlugin> supplier){
        if(str == null || "".equals(str)){
            return Collections.emptyList();
        }
        String[] all = str.split(SPLIT_ALL);
        if(all.length == 0){
            return Collections.emptyList();
        }
        List<DependencyPlugin> list = new ArrayList<>(all.length);
        for (String s : all) {
            String[] one = s.split(SPLIT_ONE);
            if(one.length == 0){
                continue;
            }
            if(one.length != 3){
                continue;
            }
            AbstractDependencyPlugin dependencyPlugin = supplier.get();
            dependencyPlugin.setId(one[0]);
            dependencyPlugin.setVersion(one[1]);
            dependencyPlugin.setOptional("true".equalsIgnoreCase(one[2]));
            list.add(dependencyPlugin);
        }
        return list;
    }

}
