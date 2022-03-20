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

import com.gitee.starblues.common.PackageType;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.Repackager;
import com.gitee.starblues.utils.ObjectUtils;
import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * 主程序打包
 * @author starBlues
 * @version 3.0.0
 */
@Getter
public class MainRepackager implements Repackager {

    private final RepackageMojo repackageMojo;
    private final MainConfig mainConfig;

    public MainRepackager(RepackageMojo repackageMojo) {
        this.repackageMojo = repackageMojo;
        this.mainConfig = repackageMojo.getMainConfig();
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        checkConfig();
        String packageType = mainConfig.getPackageType();
        if(PackageType.MAIN_PACKAGE_TYPE_JAR.equalsIgnoreCase(packageType)){
            new JarNestPackager(this).repackage();
        } else if(PackageType.MAIN_PACKAGE_TYPE_JAR_OUTER.equalsIgnoreCase(packageType)){
            new JarOuterPackager(this).repackage();
        } else {
            throw new MojoFailureException("Not found packageType : " + packageType);
        }
    }

    private void checkConfig() throws MojoFailureException {
        if(mainConfig == null){
            throw new MojoFailureException("configuration.mainConfig config cannot be empty");
        }
        if(ObjectUtils.isEmpty(mainConfig.getMainClass())) {
            throw new MojoFailureException("configuration.mainConfig.mainClass config cannot be empty");
        }
        String fileName = mainConfig.getFileName();
        if(ObjectUtils.isEmpty(fileName)) {
            MavenProject project = repackageMojo.getProject();
            mainConfig.setFileName(project.getArtifactId() + "-" + project.getVersion() + "-repackage");
        }
        String packageType = mainConfig.getPackageType();
        if(ObjectUtils.isEmpty(packageType)) {
            mainConfig.setPackageType(PackageType.MAIN_PACKAGE_TYPE_JAR);
        }
        String outputDirectory = mainConfig.getOutputDirectory();
        if(ObjectUtils.isEmpty(outputDirectory)){
            mainConfig.setOutputDirectory(repackageMojo.getOutputDirectory().getPath());
        }
    }


}
