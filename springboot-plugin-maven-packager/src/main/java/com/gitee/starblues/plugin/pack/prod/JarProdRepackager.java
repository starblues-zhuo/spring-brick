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

package com.gitee.starblues.plugin.pack.prod;

import com.gitee.starblues.plugin.pack.RepackageMojo;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;

/**
 * jar包生成
 * @author starBlues
 * @version 3.0.0
 */
public class JarProdRepackager extends ZipProdRepackager {


    public JarProdRepackager(RepackageMojo repackageMojo, ProdConfig prodConfig) {
        super(repackageMojo, prodConfig);
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        super.repackage();
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
