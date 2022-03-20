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

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;

import java.io.File;
import java.io.FileOutputStream;

/**
 * jar 打包工具
 * @author starBlues
 * @version 3.0.0
 */
public class PackageJar extends PackageZip{
    public PackageJar(File file) throws Exception {
        super(file);
    }

    public PackageJar(String outputDirectory, String packageName) throws Exception {
        super(outputDirectory, packageName);
    }

    @Override
    protected String getPackageFileSuffix() {
        return "jar";
    }

    @Override
    protected ArchiveOutputStream getOutputStream(File packFile) throws Exception {
        return new JarArchiveOutputStream(new FileOutputStream(packFile));
    }

    @Override
    protected ZipArchiveEntry getArchiveEntry(String name) {
        return new JarArchiveEntry(name);
    }
}
