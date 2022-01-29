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

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.plugin.pack.Constant;
import com.gitee.starblues.plugin.pack.PluginInfo;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.Repackager;
import com.gitee.starblues.plugin.pack.dev.DevRepackager;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * 生产环境打包
 * @author starBlues
 * @version 3.0.0
 */
public class ProdRepackager implements Repackager {

    @Getter
    private ProdConfig prodConfig;

    private final RepackageMojo repackageMojo;
    private final Repackager repackager;

    public ProdRepackager(RepackageMojo repackageMojo) {
        this.repackageMojo = repackageMojo;
        this.repackager = new DevRepackager(repackageMojo);
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        repackager.repackage();
        this.prodConfig = getProdConfig(repackageMojo);
        String packageType = prodConfig.getPackageType();
        Repackager repackager = null;
        if(Constant.PACKAGE_TYPE_ZIP.equalsIgnoreCase(packageType)){
            // jar
            repackager = new ZipProdRepackager(repackageMojo, prodConfig);
        }  else if(Constant.PACKAGE_TYPE_JAR.equalsIgnoreCase(packageType)){
            repackager = new JarProdRepackager(repackageMojo, prodConfig);
        } else if(Constant.PACKAGE_TYPE_DIR.equalsIgnoreCase(packageType)){
            repackager = new DirProdRepackager(repackageMojo, prodConfig);
        }  else {
            throw new MojoFailureException("Not found packageType:" + packageType);
        }
        repackager.repackage();
    }


    protected ProdConfig getProdConfig(RepackageMojo repackageMojo){
        ProdConfig prodConfig = repackageMojo.getProdConfig();
        if(prodConfig == null){
            prodConfig = new ProdConfig();
        }
        if(CommonUtils.isEmpty(prodConfig.getPackageType())){
            prodConfig.setPackageType(Constant.PACKAGE_TYPE_JAR);
        }
        String fileName = prodConfig.getFileName();
        if(CommonUtils.isEmpty(fileName)) {
            PluginInfo pluginInfo = repackageMojo.getPluginInfo();
            prodConfig.setFileName(pluginInfo.getId() + "-" + pluginInfo.getVersion());
        }
        String outputDirectory = prodConfig.getOutputDirectory();
        if(CommonUtils.isEmpty(outputDirectory)){
            prodConfig.setOutputDirectory(repackageMojo.getOutputDirectory().getPath());
        }
        Boolean includeDependencies = prodConfig.getIncludeDependencies();
        if(includeDependencies == null){
            prodConfig.setIncludeDependencies(true);
        }
        String libPath = prodConfig.getLibPath();
        if(CommonUtils.isEmpty(libPath)){
            prodConfig.setLibPath(PackageStructure.LIB_NAME);
        }
        return prodConfig;
    }

}
