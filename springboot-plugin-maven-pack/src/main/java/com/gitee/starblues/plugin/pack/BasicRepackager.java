package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.plugin.pack.dev.DevConfig;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.gitee.starblues.common.PackageStructure.*;
import static com.gitee.starblues.common.PluginDescriptorKey.*;
import static com.gitee.starblues.plugin.pack.Constant.SCOPE_PROVIDED;
import static com.gitee.starblues.plugin.pack.utils.CommonUtils.isEmpty;
import static com.gitee.starblues.plugin.pack.utils.CommonUtils.joinPath;

/**
 * @author starBlues
 * @version 1.0
 */

public class BasicRepackager implements Repackager{

    @Getter
    private String rootDir;
    private String relativeManifestPath;
    private String relativeLibIndexPath;

    protected final RepackageMojo repackageMojo;

    public BasicRepackager(RepackageMojo repackageMojo) {
        this.repackageMojo = repackageMojo;
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        rootDir = createRootDir();
        relativeManifestPath = getRelativeManifestPath();
        relativeLibIndexPath = getRelativeLibIndexPath();
        try {
            Manifest manifest = getManifest();
            writeManifest(manifest);
        } catch (Exception e) {
            repackageMojo.getLog().error(e.getMessage(), e);
            throw new MojoFailureException(e);
        }
    }

    protected String getRelativeManifestPath(){
        return MANIFEST;
    }

    protected String getRelativeLibIndexPath(){
        return LIB_INDEX_NAME;
    }

    protected String createRootDir() throws MojoFailureException {
        String rootDirPath = getBasicRootDir();
        File rootDir = new File(rootDirPath);
        rootDir.deleteOnExit();
        if(rootDir.mkdir()){
            return rootDirPath;
        }
        throw new MojoFailureException("创建插件根目录失败. " + rootDirPath);
    }

    protected String getBasicRootDir(){
        File outputDirectory = repackageMojo.getOutputDirectory();
        return joinPath(outputDirectory.getPath(), PackageStructure.META_INF_NAME);
    }

    protected void writeManifest(Manifest manifest) throws Exception {
        String manifestPath = CommonUtils.joinPath(rootDir, resolvePath(this.relativeManifestPath));
        File file = new File(manifestPath);
        FileOutputStream outputStream = null;
        try {
            FileUtils.forceMkdirParent(file);
            if(file.createNewFile()){
                outputStream = new FileOutputStream(file, false);
                manifest.write(outputStream);
            }
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    repackageMojo.getLog().error(e.getMessage(), e);
                }
            }
        }
    }

    protected Manifest getManifest() throws Exception{
        PluginInfo pluginInfo = repackageMojo.getPluginInfo();
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue("Manifest-Version", "1.0");
        attributes.putValue(PLUGIN_ID, pluginInfo.getId());
        attributes.putValue(PLUGIN_BOOTSTRAP_CLASS, pluginInfo.getBootstrapClass());
        attributes.putValue(PLUGIN_VERSION, pluginInfo.getVersion());
        attributes.putValue(PLUGIN_PATH, getPluginPath());

        String libFilePath = writeLibFile();
        if(!CommonUtils.isEmpty(libFilePath)){
            attributes.putValue(PLUGIN_LIB_INDEX, libFilePath);
        }
        String configFileName = pluginInfo.getConfigFileName();
        if(!isEmpty(configFileName)){
            attributes.putValue(PLUGIN_CONFIG_FILE_NAME, configFileName);
        }
        String provider = pluginInfo.getProvider();
        if(!isEmpty(provider)){
            attributes.putValue(PLUGIN_PROVIDER, provider);
        }
        String requires = pluginInfo.getRequires();
        if(!isEmpty(requires)){
            attributes.putValue(PLUGIN_REQUIRES, requires);
        }
        String dependencies = pluginInfo.getDependencies();
        if(!isEmpty(dependencies)){
            attributes.putValue(PLUGIN_DEPENDENCIES, dependencies);
        }
        String description = pluginInfo.getDescription();
        if(!isEmpty(description)){
            attributes.putValue(PLUGIN_DESCRIPTION, description);
        }
        String license = pluginInfo.getLicense();
        if(!isEmpty(license)){
            attributes.putValue(PLUGIN_LICENSE, license);
        }
        // TODO 未添加全
        return manifest;
    }

    protected String getPluginPath(){
        DevConfig devConfig = repackageMojo.getDevConfig();
        if(devConfig != null && !isEmpty(devConfig.getPluginPath())){
            return devConfig.getPluginPath();
        }
        return repackageMojo.getProject().getBuild().getOutputDirectory();
    }

    protected String writeLibFile() throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        Set<String> libIndex = getLibIndexSet();
        for (String index : libIndex) {
            stringBuilder.append(index).append("\n");
        }
        String content = stringBuilder.toString();
        String path = joinPath(rootDir, resolvePath(relativeLibIndexPath));
        try {
            File file = new File(path);
            FileUtils.forceMkdirParent(file);
            if(file.createNewFile()){
                FileUtils.write(file, content, "utf-8");
                return path;
            }
            throw new Exception();
        } catch (Exception e){
            throw new IOException("Create " + path + " file error");
        }
    }

    protected Set<String> getLibIndexSet() throws Exception {
        Set<Artifact> dependencies = repackageMojo.getDependencies();
        Set<String> libPaths = new HashSet<>(dependencies.size());
        for (Artifact artifact : dependencies) {
            if(filterArtifact(artifact)){
                continue;
            }
            libPaths.add(getLibIndex(artifact));
        }
        return libPaths;
    }

    protected String getLibIndex(Artifact artifact){
        return artifact.getFile().getPath();
    }

    /**
     * 过滤Artifact
     * @param artifact Artifact
     * @return 返回true表示被过滤掉
     */
    protected boolean filterArtifact(Artifact artifact){
        return SCOPE_PROVIDED.equalsIgnoreCase(artifact.getScope());
    }


}
