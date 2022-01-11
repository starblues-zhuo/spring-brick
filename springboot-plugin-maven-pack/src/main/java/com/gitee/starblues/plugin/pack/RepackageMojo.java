package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.plugin.pack.dev.DevConfig;
import com.gitee.starblues.plugin.pack.dev.DevRepackager;
import com.gitee.starblues.plugin.pack.prod.ProdConfig;
import com.gitee.starblues.plugin.pack.prod.ProdRepackager;
import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @author starBlues
 * @version 1.0
 */
@Mojo(name = "repackage", defaultPhase = LifecyclePhase.PACKAGE, requiresProject = true, threadSafe = true,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME)
@Getter
public class RepackageMojo extends AbstractPackagerMojo {


    @Parameter(property = "springboot-plugin.devConfig")
    private DevConfig devConfig;

    @Parameter(property = "springboot-plugin.prodConfig")
    private ProdConfig prodConfig;

    @Override
    protected void pack() throws MojoExecutionException, MojoFailureException {
        String mode = getMode();

        if(Constant.MODE_PROD.equalsIgnoreCase(mode)){
            new ProdRepackager(this).repackage();
        } else if(Constant.MODE_DEV.equalsIgnoreCase(mode)){
            new DevRepackager(this).repackage();
        } else {
            throw new MojoExecutionException(mode  +" model not supported, mode support : "
                    + Constant.MODE_DEV + "/" + Constant.MODE_PROD);
        }
    }


}
