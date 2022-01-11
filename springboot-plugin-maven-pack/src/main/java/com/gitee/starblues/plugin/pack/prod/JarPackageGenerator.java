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
 * @author starBlues
 * @version 1.0
 */
public class JarPackageGenerator extends ZipProdRepackager {


    public JarPackageGenerator(RepackageMojo repackageMojo, ProdConfig prodConfig) {
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
