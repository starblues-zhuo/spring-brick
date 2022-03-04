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
import com.gitee.starblues.plugin.pack.Constant;
import com.gitee.starblues.plugin.pack.PluginInfo;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.Repackager;
import com.gitee.starblues.plugin.pack.dev.DevRepackager;
import com.gitee.starblues.utils.ObjectUtils;
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

        if(PackageType.PLUGIN_PACKAGE_TYPE_ZIP.equalsIgnoreCase(packageType)){
            repackager = new ZipProdRepackager(repackageMojo, prodConfig);
        }  else if(PackageType.PLUGIN_PACKAGE_TYPE_JAR.equalsIgnoreCase(packageType)){
            repackager = new JarNestedProdRepackager(repackageMojo, prodConfig);
        } else if(PackageType.PLUGIN_PACKAGE_TYPE_ZIP_OUTER.equalsIgnoreCase(packageType)){
            repackager = new ZipOuterProdRepackager(repackageMojo, prodConfig);
        } else if(PackageType.PLUGIN_PACKAGE_TYPE_JAR_OUTER.equalsIgnoreCase(packageType)){
            repackager = new JarOuterProdRepackager(repackageMojo, prodConfig);
        } else if(PackageType.PLUGIN_PACKAGE_TYPE_DIR.equalsIgnoreCase(packageType)){
            repackager = new DirProdRepackager(repackageMojo, prodConfig);
        }  else {
            throw new MojoFailureException("Not found packageType : " + packageType);
        }
        repackager.repackage();
    }

    protected ProdConfig getProdConfig(RepackageMojo repackageMojo){
        ProdConfig prodConfig = repackageMojo.getProdConfig();
        if(prodConfig == null){
            prodConfig = new ProdConfig();
        }
        if(ObjectUtils.isEmpty(prodConfig.getPackageType())){
            prodConfig.setPackageType(PackageType.PLUGIN_PACKAGE_TYPE_JAR);
        }
        String fileName = prodConfig.getFileName();
        if(ObjectUtils.isEmpty(fileName)) {
            PluginInfo pluginInfo = repackageMojo.getPluginInfo();
            prodConfig.setFileName(pluginInfo.getId() + "-" + pluginInfo.getVersion() + "-repackage");
        }
        String outputDirectory = prodConfig.getOutputDirectory();
        if(ObjectUtils.isEmpty(outputDirectory)){
            prodConfig.setOutputDirectory(repackageMojo.getOutputDirectory().getPath());
        }
        return prodConfig;
    }



}
