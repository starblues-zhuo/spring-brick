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
import com.gitee.starblues.plugin.pack.Constant;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.dev.Dependency;
import com.gitee.starblues.plugin.pack.dev.DevConfig;
import com.gitee.starblues.plugin.pack.dev.DevRepackager;
import com.gitee.starblues.plugin.pack.utils.PackageZip;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.*;

/**
 * zip 打包
 * @author starBlues
 * @version 3.0.0
 */
public class ZipProdRepackager extends DevRepackager {


    protected final ProdConfig prodConfig;

    protected PackageZip packageZip;

    public ZipProdRepackager(RepackageMojo repackageMojo, ProdConfig prodConfig) {
        super(repackageMojo);
        this.prodConfig = prodConfig;
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        try {
            packageZip = getPackageZip();
            super.repackage();
            resolveClasses();
            resolveResourcesDefine();
            String rootDir = getRootDir();
            try {
                FileUtils.deleteDirectory(new File(rootDir));
            } catch (IOException e) {
                // 忽略
            }
            repackageMojo.getLog().info("Success package prod zip file : "
                    + packageZip.getFile().getPath());
        } catch (Exception e){
            repackageMojo.getLog().error(e.getMessage(), e);
            throw new MojoFailureException(e);
        } finally {
            if(packageZip != null){
                IOUtils.closeQuietly(packageZip);
            }
        }
    }

    protected PackageZip getPackageZip() throws Exception {
        return new PackageZip(prodConfig.getOutputDirectory(), prodConfig.getFileName());
    }

    @Override
    protected String getBasicRootDir(){
        File outputDirectory = repackageMojo.getOutputDirectory();
        return FilesUtils.joiningFilePath(outputDirectory.getPath(), UUID.randomUUID().toString());
    }

    @Override
    protected Map<String, Dependency> getModuleDependencies(DevConfig devConfig) {
        // 将项目中模块依赖置为空
        return Collections.emptyMap();
    }

    @Override
    protected String getPluginPath() {
        return CLASSES_NAME + SEPARATOR;
    }

    @Override
    protected String getLibIndex(Artifact artifact){
        return PROD_LIB_PATH + artifact.getFile().getName() + repackageMojo.resolveLoadToMain(artifact);
    }

    @Override
    protected boolean filterArtifact(Artifact artifact) {
        return Constant.scopeFilter(artifact.getScope());
    }

    protected void resolveClasses() throws Exception {
        String buildDir = repackageMojo.getProject().getBuild().getOutputDirectory();
        packageZip.copyDirToPackage(new File(buildDir), null);
    }

    @Override
    protected Manifest getManifest() throws Exception {
        Manifest manifest = super.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue(ManifestKey.PLUGIN_META_PATH, PROD_PLUGIN_META_PATH);
        attributes.putValue(ManifestKey.PLUGIN_PACKAGE_TYPE, PackageType.PLUGIN_PACKAGE_TYPE_ZIP);
        return manifest;
    }

    @Override
    protected Properties createPluginMetaInfo() throws Exception {
        Properties properties = super.createPluginMetaInfo();
        properties.put(PluginDescriptorKey.PLUGIN_RESOURCES_CONFIG, PROD_RESOURCES_DEFINE_PATH);
        return properties;
    }

    @Override
    protected void writeManifest(Manifest manifest) throws Exception {
        packageZip.writeManifest(manifest);
    }

    @Override
    protected String writePluginMetaInfo(Properties properties) throws Exception {
        packageZip.write(PROD_PLUGIN_META_PATH, outputStream->{
            properties.store(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
                    Constant.PLUGIN_METE_COMMENTS);
        });
        return PROD_PLUGIN_META_PATH;
    }

    protected void resolveResourcesDefine() throws Exception{
        Set<String> dependencyIndexNames = resolveDependencies();
        StringBuilder content = new StringBuilder();
        content.append(RESOURCES_DEFINE_DEPENDENCIES).append("\n");
        for (String dependencyIndexName : dependencyIndexNames) {
            content.append(dependencyIndexName).append("\n");
        }
        String loadMainResources = super.getLoadMainResources();
        if(!ObjectUtils.isEmpty(loadMainResources)){
            content.append(loadMainResources).append("\n");
        }
        final byte[] bytes = content.toString().getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            packageZip.putInputStreamEntry(PROD_RESOURCES_DEFINE_PATH, byteArrayInputStream);
        }
    }

    protected Set<String> resolveDependencies() throws Exception {
        Set<Artifact> dependencies = repackageMojo.getFilterDependencies();
        String libDirEntryName = createLibEntry();
        Set<String> dependencyIndexNames = new HashSet<>(dependencies.size());
        for (Artifact artifact : dependencies) {
            if(filterArtifact(artifact)){
                continue;
            }
            String dependencyIndexName = packageZip.writeDependency(artifact.getFile(), libDirEntryName);
            dependencyIndexNames.add(dependencyIndexName);
        }
        return dependencyIndexNames;
    }

    protected String createLibEntry() throws Exception {
        String libDirEntryName = PROD_LIB_PATH;
        packageZip.putDirEntry(libDirEntryName);
        return libDirEntryName;
    }

}
