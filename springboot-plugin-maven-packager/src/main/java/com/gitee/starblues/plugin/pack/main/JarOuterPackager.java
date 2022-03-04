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

package com.gitee.starblues.plugin.pack.main;

import com.gitee.starblues.common.ManifestKey;
import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.utils.FilesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.ManifestKey.*;
import static com.gitee.starblues.common.ManifestKey.MANIFEST_VERSION_1_0;

/**
 * jar 外置包
 * @author starBlues
 * @version 3.0.0
 */
public class JarOuterPackager extends JarNestPackager {

    private final List<String> dependenciesName = new ArrayList<>();

    public JarOuterPackager(MainRepackager mainRepackager) {
        super(mainRepackager);
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        // 生成依赖文件夹
        String rootDir = createRootDir();
        mainConfig.setOutputDirectory(rootDir);
        super.repackage();
    }

    @Override
    protected void writeClasses() throws Exception {
        String buildDir = repackageMojo.getProject().getBuild().getOutputDirectory();
        packageJar.copyDirToPackage(new File(buildDir), "");
    }

    private String createRootDir() throws MojoFailureException{
        String outputDirectory = mainConfig.getOutputDirectory();
        String fileName = mainConfig.getFileName();
        String rootDirPath = FilesUtils.joiningFilePath(outputDirectory, fileName);
        File rootFile = new File(rootDirPath);
        if(rootFile.exists()){
            rootFile.deleteOnExit();
        }
        if(rootFile.mkdirs()){
            return rootDirPath;
        } else {
            throw new MojoFailureException("Create dir failure : " + rootDirPath);
        }
    }

    @Override
    protected Manifest getManifest() throws Exception {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue(MANIFEST_VERSION, MANIFEST_VERSION_1_0);
        attributes.remove(START_CLASS);
        attributes.putValue(MAIN_CLASS, mainConfig.getMainClass());
        if(dependenciesName.isEmpty()){
            return manifest;
        }
        String classPathStr = String.join(" ", dependenciesName);
        attributes.putValue(ManifestKey.CLASS_PATH, classPathStr);
        return manifest;
    }

    @Override
    protected void writeDependencies() throws Exception {
        Set<Artifact> dependencies = repackageMojo.getSourceDependencies();
        for (Artifact artifact : dependencies) {
            if(filterArtifact(artifact)){
                continue;
            }
            File artifactFile = artifact.getFile();
            String targetFilePath = FilesUtils.joiningFilePath(
                    mainConfig.getOutputDirectory(), PackageStructure.LIB_NAME, artifactFile.getName());

            FileUtils.copyFile(artifactFile, new File(targetFilePath));
            dependenciesName.add(PackageStructure.LIB_NAME + "/" + artifactFile.getName());
        }
    }

}
