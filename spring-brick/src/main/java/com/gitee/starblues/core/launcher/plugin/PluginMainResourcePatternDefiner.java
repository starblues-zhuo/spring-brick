/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.launcher.JavaMainResourcePatternDefiner;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.SpringBeanCustomUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 定义插件从主程序加载资源的匹配
 * @author starBlues
 * @version 3.0.0
 */
public class PluginMainResourcePatternDefiner extends JavaMainResourcePatternDefiner {

    private static final String FRAMEWORK = "com/gitee/starblues/**";

    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

    private final String mainPackage;
    private final InsidePluginDescriptor descriptor;
    private final BasicMainResourcePatternDefiner basicPatternDefiner;

    public PluginMainResourcePatternDefiner(PluginInteractive pluginInteractive) {
        mainPackage = pluginInteractive.getConfiguration().mainPackage();
        this.descriptor = pluginInteractive.getPluginDescriptor();
        basicPatternDefiner = getPatternDefiner(pluginInteractive);
    }

    @Override
    public Set<String> getIncludePatterns() {
        Set<String> includeResourcePatterns = super.getIncludePatterns();
        Set<String> includePatterns = basicPatternDefiner.getIncludePatterns();
        if(!ObjectUtils.isEmpty(includePatterns)){
            includeResourcePatterns.addAll(includePatterns);
        } else {
            includeResourcePatterns.add(ObjectUtils.changePackageToMatch(mainPackage));
        }
        includeResourcePatterns.add(FRAMEWORK);
        addWebIncludeResourcePatterns(includeResourcePatterns);
        addApiDoc(includeResourcePatterns);
        addDbDriver(includeResourcePatterns);

        // 配置插件自定义从主程序加载的资源匹配
        Set<String> includeMainResourcePatterns = descriptor.getIncludeMainResourcePatterns();
        if(ObjectUtils.isEmpty(includeMainResourcePatterns)){
            return includeResourcePatterns;
        }

        for (String includeMainResourcePattern : includeMainResourcePatterns) {
            if(ObjectUtils.isEmpty(includeMainResourcePattern)){
                continue;
            }
            includeResourcePatterns.add(includeMainResourcePattern);
        }
        return includeResourcePatterns;
    }



    @Override
    public Set<String> getExcludePatterns() {
        Set<String> excludeResourcePatterns = new HashSet<>();
        Set<String> excludePatterns = basicPatternDefiner.getExcludePatterns();
        if(!ObjectUtils.isEmpty(excludePatterns)){
            excludeResourcePatterns.addAll(excludePatterns);
        }
        Set<String> excludeMainResourcePatterns = descriptor.getExcludeMainResourcePatterns();
        if(!ObjectUtils.isEmpty(excludeMainResourcePatterns)){
            excludeResourcePatterns.addAll(excludeMainResourcePatterns);
        }
        excludeResourcePatterns.add(FACTORIES_RESOURCE_LOCATION);
        return excludeResourcePatterns;
    }

    protected void addWebIncludeResourcePatterns(Set<String> patterns){
        patterns.add("org/springframework/web/**");
        patterns.add("org/springframework/http/**");
        patterns.add("org/springframework/remoting/**");
        patterns.add("org/springframework/ui/**");

        patterns.add("com/fasterxml/jackson/**");

    }

    protected void addApiDoc(Set<String> patterns){
        patterns.add("springfox/documentation/**");
        patterns.add("io/swagger/**");
        patterns.add("org/springdoc/**");
    }

    protected void addDbDriver(Set<String> patterns){
        // mysql
        patterns.add("com/mysql/**");
        // oracle
        patterns.add("oracle/jdbc/**");
        // sqlserver
        patterns.add("com/microsoft/jdbc/sqlserver/**");
        // DB2
        patterns.add("com/ibm/db2/jdbc/**");
        // DB2/AS400
        patterns.add("com/ibm/as400/**");
        // Informix
        patterns.add("com/informix/jdbc/**");
        // Hypersonic
        patterns.add("org/hsql/**");
        // MS SQL
        patterns.add("com/microsoft/jdbc/**");
        // Postgres
        patterns.add("org/postgresql/**");
        // Sybase
        patterns.add("com/sybase/jdbc2/**");
        // Weblogic
        patterns.add("weblogic/jdbc/**");
        // h2
        patterns.add("jdbc/h2/**");
    }

    /**
     * 获取基本的 MainResourcePatternDefiner
     * @param pluginInteractive PluginInteractive
     * @return BasicMainResourcePatternDefiner
     */
    private BasicMainResourcePatternDefiner getPatternDefiner(PluginInteractive pluginInteractive){
        final MainApplicationContext mainApplicationContext = pluginInteractive.getMainApplicationContext();
        BasicMainResourcePatternDefiner definer = SpringBeanCustomUtils.getExistBean(
                mainApplicationContext, BasicMainResourcePatternDefiner.class);
        if(definer == null){
            return new BasicMainResourcePatternDefiner(mainPackage);
        } else {
            return definer;
        }
    }



}
