package com.gitee.starblues.plugin.pack.prod;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.dev.DevRepackager;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.*;

/**
 * @author starBlues
 * @version 1.0
 */
public class DirPackageGenerator extends DevRepackager {

    private final ProdConfig prodConfig;


    public DirPackageGenerator(RepackageMojo repackageMojo, ProdConfig prodConfig) {
        super(repackageMojo);
        this.prodConfig = prodConfig;
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        super.repackage();
        try {
            resolveClasses();
        } catch (Exception e) {
            repackageMojo.getLog().error(e.getMessage(), e);
            throw new MojoFailureException(e);
        }
    }

    @Override
    protected String createRootDir() throws MojoFailureException {
        String fileName = prodConfig.getFileName();
        String dirPath = CommonUtils.joinPath(prodConfig.getOutputDirectory(), fileName);
        File dirFile = new File(dirPath);
        if(dirFile.exists() && dirFile.isFile()){
            int i = 0;
            while (true){
                dirFile = new File(dirPath + "_" + i);
                if(dirFile.exists() && dirFile.isFile()){
                    i = i + 1;
                    continue;
                }
                break;
            }
        }
        dirFile.deleteOnExit();
        if(!dirFile.mkdirs()){
            throw new MojoFailureException("Create package dir failure: " + dirFile.getPath());
        }
        return dirFile.getPath();
    }

    @Override
    protected String getRelativeManifestPath() {
        return CommonUtils.joinPath(META_INF_NAME, MANIFEST);
    }

    @Override
    protected String getRelativeResourcesDefinePath() {
        return CommonUtils.joinPath(META_INF_NAME, RESOURCES_DEFINE_NAME);
    }

    @Override
    protected Manifest getManifest() throws Exception {
        Manifest manifest = super.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue(PluginDescriptorKey.PLUGIN_PATH, CLASSES_NAME);
        attributes.putValue(PluginDescriptorKey.PLUGIN_RESOURCES_CONFIG, PROD_RESOURCES_DEFINE_PATH);
        return manifest;
    }

    protected void resolveClasses() throws Exception {
        String buildDir = repackageMojo.getProject().getBuild().getOutputDirectory();
        String path = CommonUtils.joinPath(getRootDir(), CLASSES_NAME);
        File file = new File(path);
        FileUtils.forceMkdir(file);
        FileUtils.copyDirectory(new File(buildDir), file);
    }

    @Override
    protected Set<String> getDependenciesIndexSet() throws Exception {
        Set<Artifact> dependencies = repackageMojo.getDependencies();
        String libDir = createLibDir();
        Set<String> dependencyIndexNames = new HashSet<>(dependencies.size());
        for (Artifact artifact : dependencies) {
            if(filterArtifact(artifact)){
                continue;
            }
            File artifactFile = artifact.getFile();
            FileUtils.copyFile(artifactFile, new File(CommonUtils.joinPath(libDir, artifactFile.getName())));
            dependencyIndexNames.add(PackageStructure.PROD_LIB_PATH + artifactFile.getName());
        }
        return dependencyIndexNames;
    }

    protected String createLibDir() throws IOException {
        String dir = CommonUtils.joinPath(getRootDir(), PackageStructure.LIB_NAME);
        File file = new File(dir);
        if(file.mkdir()){
            return dir;
        }
        throw new IOException("Create " + PackageStructure.LIB_NAME + " dir failure");
    }

}
