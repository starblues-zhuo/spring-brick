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

import com.gitee.starblues.common.PackageStructure;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Manifest;

/**
 * 开发环境 PluginDescriptorLoader 加载者
 * @author starBlues
 * @version 3.0.0
 */
public class DevPluginDescriptorLoader extends AbstractPluginDescriptorLoader{


    @Override
    protected Manifest getManifest(Path location) throws Exception {
        String manifestPath = location.toString() + File.separator + PackageStructure.MANIFEST;
        File file = new File(manifestPath);
        if(!file.exists()){
            return null;
        }
        Path path = Paths.get(manifestPath);
        try {
            return super.getManifest(Files.newInputStream(path));
        } finally {
            try {
                path.getFileSystem().close();
            } catch (Exception e) {
                // 忽略
            }
        }
    }

    @Override
    protected DefaultInsidePluginDescriptor create(Manifest manifest, Path path) throws Exception {
        final DefaultInsidePluginDescriptor descriptor = super.create(manifest, path);
        descriptor.setType(PluginType.DEV);
        return descriptor;
    }
}
