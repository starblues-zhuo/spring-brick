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

package com.gitee.starblues.bootstrap.processor.web.thymeleaf;

import com.gitee.starblues.spring.web.thymeleaf.ThymeleafConfig;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.core.env.Environment;

/**
 * 解析ThymeleafConfig配置
 * @author starBlues
 * @version 3.0.0
 */
public class ThymeleafConfigParse {

    private static final String KEY_PREFIX = "spring.thymeleaf.";


    public static final String ENABLED = KEY_PREFIX + "enabled";
    private static final String PREFIX = KEY_PREFIX + "prefix";
    private static final String SUFFIX = KEY_PREFIX + "suffix";
    private static final String MODE = KEY_PREFIX + "mode";
    private static final String CACHE = KEY_PREFIX + "cache";
    private static final String TEMPLATE_RESOLVER_ORDER = KEY_PREFIX + "templateResolverOrder";


    public static ThymeleafConfig parse(Environment environment){
        ThymeleafConfig thymeleafConfig = new ThymeleafConfig();
        String enabled = environment.getProperty(ENABLED);
        if(!ObjectUtils.isEmpty(enabled) && !Boolean.parseBoolean(enabled)) {
            thymeleafConfig.setEnabled(false);
            return thymeleafConfig;
        }
        String prefix = environment.getProperty(PREFIX);
        if(!ObjectUtils.isEmpty(prefix)){
            thymeleafConfig.setPrefix(prefix);
        }
        String suffix = environment.getProperty(SUFFIX);
        if(!ObjectUtils.isEmpty(suffix)){
            thymeleafConfig.setSuffix(suffix);
        }
        String mode = environment.getProperty(MODE);
        if(!ObjectUtils.isEmpty(mode)){
            thymeleafConfig.setMode(mode);
        }
        String cache = environment.getProperty(CACHE);
        if(!ObjectUtils.isEmpty(cache)){
            thymeleafConfig.setCache(Boolean.getBoolean(cache));
        }
        String templateResolverOrder = environment.getProperty(TEMPLATE_RESOLVER_ORDER);
        if(!ObjectUtils.isEmpty(templateResolverOrder)){
            thymeleafConfig.setTemplateResolverOrder(Integer.getInteger(templateResolverOrder));
        }
        return thymeleafConfig;
    }



}
