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

package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * 生产环境插件描述加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PluginDescriptorLoader target;

    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        if(ResourceUtils.isJarFile(location)){
            target = new ProdPackagePluginDescriptorLoader(PluginType.JAR);
        } else if(ResourceUtils.isZipFile(location)){
            target = new ProdPackagePluginDescriptorLoader(PluginType.ZIP);
        } else if(ResourceUtils.isDirFile(location)){
            target = new ProdDirPluginDescriptorLoader();
        } else {
            logger.warn("不能解析文件: {}", location);
            return null;
        }
        return target.load(location);
    }

    @Override
    public void close() throws Exception {
        target.close();
    }
}
