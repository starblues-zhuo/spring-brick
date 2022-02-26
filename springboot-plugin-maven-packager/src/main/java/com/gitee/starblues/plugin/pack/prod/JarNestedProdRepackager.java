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

import com.gitee.starblues.common.PackageType;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.utils.PackageJar;
import com.gitee.starblues.plugin.pack.utils.PackageZip;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.PROD_CLASSES_PATH;
import static com.gitee.starblues.common.PackageStructure.PROD_RESOURCES_DEFINE_PATH;

/**
 * jar包生成
 * @author starBlues
 * @version 3.0.0
 */
public class JarNestedProdRepackager extends ZipProdRepackager {


    public JarNestedProdRepackager(RepackageMojo repackageMojo, ProdConfig prodConfig) {
        super(repackageMojo, prodConfig);
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        super.repackage();
    }

    @Override
    protected PackageZip getPackageZip() throws Exception {
        return new PackageJar(prodConfig.getOutputDirectory(), prodConfig.getFileName());
    }

    @Override
    protected Manifest getManifest() throws Exception {
        Manifest manifest = super.getManifest();
        manifest.getMainAttributes().putValue(PluginDescriptorKey.PLUGIN_PACKAGE_TYPE, PackageType.PLUGIN_PACKAGE_TYPE_JAR);
        return manifest;
    }


}
