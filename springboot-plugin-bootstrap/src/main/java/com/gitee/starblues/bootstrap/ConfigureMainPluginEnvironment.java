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

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.annotation.OneselfConfig;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.utils.AnnotationUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ResourceUtils;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 插件环境配置
 * @author starBlues
 * @version 3.0.0
 */
class ConfigureMainPluginEnvironment {

    private final ProcessorContext processorContext;
    private final List<PropertySourceLoader> propertySourceLoaders;

    ConfigureMainPluginEnvironment(ProcessorContext processorContext) {
        this.processorContext = processorContext;

        this.propertySourceLoaders = new ArrayList<>(2);
        this.propertySourceLoaders.add(new YamlPropertySourceLoader());
        this.propertySourceLoaders.add(new PropertiesPropertySourceLoader());
    }

    void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        SpringPluginBootstrap springPluginBootstrap = processorContext.getSpringPluginBootstrap();
        OneselfConfig oneselfConfig = AnnotationUtils.findAnnotation(springPluginBootstrap.getClass(),
                OneselfConfig.class);
        if(oneselfConfig == null){
            return;
        }
        String[] mainConfigFileName = oneselfConfig.mainConfigFileName();
        if(mainConfigFileName.length == 0){
            return;
        }
        for (String fileName : mainConfigFileName) {
            load(environment, fileName);
        }
    }


    private void load(ConfigurableEnvironment environment, String fileName){
        String fileSuffix = ResourceUtils.getFileSuffix(fileName);
        if(ObjectUtils.isEmpty(fileSuffix)){
            return;
        }
        PropertySourceLoader sourceLoader = null;
        for (PropertySourceLoader propertySourceLoader : propertySourceLoaders) {
            String[] fileExtensions = propertySourceLoader.getFileExtensions();
            for (String fileExtension : fileExtensions) {
                if(fileSuffix.equalsIgnoreCase(fileExtension)){
                    sourceLoader = propertySourceLoader;
                    break;
                }
            }
        }
        if(sourceLoader == null){
            return;
        }
        URL url = Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResource(fileName);
        if(url == null){
            return;
        }
        try {
            Path path = Paths.get(url.toURI());
            Resource resource = new FileSystemResource(path);
            List<PropertySource<?>> propertySources = sourceLoader.load(fileName, resource);
            for (PropertySource<?> propertySource : propertySources) {
                environment.getPropertySources().addFirst(propertySource);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
