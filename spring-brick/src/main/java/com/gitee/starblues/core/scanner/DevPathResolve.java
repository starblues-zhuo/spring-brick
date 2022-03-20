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

package com.gitee.starblues.core.scanner;

import com.gitee.starblues.common.PackageStructure;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 开发环境路径解决器
 * @author starBlues
 * @version 3.0.0
 */
public class DevPathResolve implements PathResolve{

    private final List<String> devCompilePackageNames = new ArrayList<>();

    public DevPathResolve() {
        addCompilePackageName();
    }

    protected void addCompilePackageName(){
        // 添加插件信息查询目录
        devCompilePackageNames.add("target".concat(File.separator).concat(PackageStructure.META_INF_NAME));
    }

    @Override
    public Path resolve(Path path) {
        for (String devCompilePackageName : devCompilePackageNames) {
            String compilePackagePathStr = path.toString() + File.separator + devCompilePackageName;
            Path compilePackagePath = Paths.get(compilePackagePathStr);
            if(Files.exists(compilePackagePath)){
                return compilePackagePath;
            }
        }
        return null;
    }
}
