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

import com.gitee.starblues.common.ManifestKey;
import com.gitee.starblues.common.PackageType;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.utils.PackageJar;
import com.gitee.starblues.plugin.pack.utils.PackageZip;
import com.gitee.starblues.utils.FilesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.*;

/**
 * zip-outer 包生成
 * @author starBlues
 * @version 3.0.0
 */
public class ZipOuterProdRepackager extends DirProdRepackager {

    protected PackageZip packageZip;

    public ZipOuterProdRepackager(RepackageMojo repackageMojo, ProdConfig prodConfig) {
        super(repackageMojo, prodConfig);
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        try {
            super.repackage();
        } catch (Exception e){
            throw new MojoFailureException(e);
        } finally {
            if(packageZip != null){
                IOUtils.closeQuietly(packageZip);
            }
        }
    }

    @Override
    protected String createRootDir() throws MojoFailureException {
        String rootDir = super.createRootDir();
        try {
            packageZip = getPackageZip(rootDir);
            return rootDir;
        } catch (Exception e) {
            throw new MojoFailureException(e);
        }
    }

    protected PackageZip getPackageZip(String rootDir) throws Exception {
        return new PackageZip(rootDir, super.prodConfig.getFileName());
    }

    @Override
    protected void resolveClasses() throws Exception {
        String buildDir = repackageMojo.getProject().getBuild().getOutputDirectory();
        packageZip.copyDirToPackage(new File(buildDir), "");
    }

    @Override
    protected Manifest getManifest() throws Exception {
        Manifest manifest = super.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue(ManifestKey.PLUGIN_META_PATH, PROD_PLUGIN_META_PATH);
        attributes.putValue(ManifestKey.PLUGIN_PACKAGE_TYPE, PackageType.PLUGIN_PACKAGE_TYPE_ZIP_OUTER);
        return manifest;
    }

    @Override
    protected Properties createPluginMetaInfo() throws Exception {
        Properties properties = super.createPluginMetaInfo();
        properties.put(PluginDescriptorKey.PLUGIN_PATH, packageZip.getFileName());
        return properties;
    }
}
