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

import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.plugin.pack.Constant;
import com.gitee.starblues.plugin.pack.RepackageMojo;
import com.gitee.starblues.plugin.pack.dev.Dependency;
import com.gitee.starblues.plugin.pack.dev.DevConfig;
import com.gitee.starblues.plugin.pack.dev.DevRepackager;
import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
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
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import static com.gitee.starblues.common.PackageStructure.*;
import static com.gitee.starblues.plugin.pack.Constant.SCOPE_PROVIDED;
import static com.gitee.starblues.plugin.pack.utils.CommonUtils.joinPath;

/**
 * zip 打包
 * @author starBlues
 * @version 3.0.0
 */
public class ZipProdRepackager extends DevRepackager {

    private static final int UNIX_FILE_MODE = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

    private static final int UNIX_DIR_MODE = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

    protected final ProdConfig prodConfig;

    protected ArchiveOutputStream outputStream;

    public ZipProdRepackager(RepackageMojo repackageMojo, ProdConfig prodConfig) {
        super(repackageMojo);
        this.prodConfig = prodConfig;
    }

    @Override
    public void repackage() throws MojoExecutionException, MojoFailureException {
        File packageFile = getPackageFile();
        try {
            outputStream = getOutputStream(packageFile);
            super.repackage();
            resolveClasses();
            resolveResourcesDefine();
            outputStream.finish();
            String rootDir = getRootDir();
            try {
                FileUtils.deleteDirectory(new File(rootDir));
            } catch (IOException e) {
                // 忽略
            }
            repackageMojo.getLog().info("Success package prod zip file : " + packageFile.getPath());
        } catch (Exception e){
            repackageMojo.getLog().error(e.getMessage(), e);
            throw new MojoFailureException(e);
        } finally {
            if(outputStream != null){
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    protected File getPackageFile() throws MojoFailureException {
        String fileSuffix = getPackageFileSuffix();
        String path = getPackageFilePath();
        File file = new File(path + "." + fileSuffix);

        if(file.exists()){
            int i = 0;
            while (true){
                file = new File(path + "_" + i + "." + fileSuffix);
                if(file.exists()){
                    i = i + 1;
                    continue;
                }
                break;
            }
        }
        try {
            if(file.createNewFile()){
                return file;
            }
            throw new IOException("Create file '" + file.getPath() + "' failure.");
        } catch (IOException e) {
            repackageMojo.getLog().error(e.getMessage(), e);
            throw new MojoFailureException("Create prod package file failure");
        }
    }

    protected String getPackageFileSuffix(){
        return "zip";
    }

    protected String getPackageFilePath(){
        return CommonUtils.joinPath(prodConfig.getOutputDirectory(), prodConfig.getFileName());
    }

    protected ArchiveOutputStream getOutputStream(File packFile) throws Exception {
        return new ZipArchiveOutputStream(new FileOutputStream(packFile));
    }

    @Override
    protected String getBasicRootDir(){
        File outputDirectory = repackageMojo.getOutputDirectory();
        return joinPath(outputDirectory.getPath(), UUID.randomUUID().toString());
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
        return PROD_LIB_PATH + artifact.getFile().getName();
    }

    @Override
    protected boolean filterArtifact(Artifact artifact) {
        return Constant.scopeFilter(artifact.getScope());
    }

    protected void resolveClasses() throws Exception {
        String buildDir = repackageMojo.getProject().getBuild().getOutputDirectory();
        copyFileToPackage(new File(buildDir), "");
    }

    @Override
    protected Manifest getManifest() throws Exception {
        Manifest manifest = super.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.putValue(PluginDescriptorKey.PLUGIN_PATH, PROD_CLASSES_PATH);
        attributes.putValue(PluginDescriptorKey.PLUGIN_RESOURCES_CONFIG, PROD_RESOURCES_DEFINE_PATH);
        return manifest;
    }

    @Override
    protected void writeManifest(Manifest manifest) throws Exception {
        putDirEntry(META_INF_NAME + SEPARATOR);
        outputStream.putArchiveEntry(getArchiveEntry(PROD_MANIFEST_PATH));
        manifest.write(outputStream);
        outputStream.closeArchiveEntry();
    }

    protected void resolveResourcesDefine() throws Exception{
        Set<String> dependencyIndexNames = resolveDependencies();
        StringBuilder content = new StringBuilder();
        content.append(RESOURCES_DEFINE_DEPENDENCIES).append("\n");
        for (String dependencyIndexName : dependencyIndexNames) {
            content.append(dependencyIndexName).append("\n");
        }
        String loadMainResources = super.getLoadMainResources();
        if(!CommonUtils.isEmpty(loadMainResources)){
            content.append(loadMainResources).append("\n");
        }
        final byte[] bytes = content.toString().getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            putInputStreamEntry(byteArrayInputStream, PROD_RESOURCES_DEFINE_PATH);
        }
    }

    protected Set<String> resolveDependencies() throws Exception {
        Set<Artifact> dependencies = repackageMojo.getDependencies();
        String libDirEntryName = createLibEntry();
        Set<String> dependencyIndexNames = new HashSet<>(dependencies.size());
        for (Artifact artifact : dependencies) {
            if(filterArtifact(artifact)){
                continue;
            }
            String dependencyIndexName = writeDependency(artifact.getFile(), libDirEntryName, outputStream);
            dependencyIndexNames.add(dependencyIndexName);
        }
        return dependencyIndexNames;
    }

    protected String createLibEntry() throws Exception {
        String libDirEntryName = PROD_LIB_PATH;
        putDirEntry(libDirEntryName);
        return libDirEntryName;
    }

    protected String writeDependency(File dependencyFile, String libDirEntryName,
                                     ArchiveOutputStream outputStream) throws Exception {
        String indexName = libDirEntryName + dependencyFile.getName();
        ZipArchiveEntry entry = getArchiveEntry(indexName);
        entry.setTime(System.currentTimeMillis());
        entry.setUnixMode(indexName.endsWith("/") ? UNIX_DIR_MODE : UNIX_FILE_MODE);
        entry.getGeneralPurposeBit().useUTF8ForNames(true);
        try(FileInputStream inputStream = new FileInputStream(dependencyFile)){
             new CrcAndSize(inputStream).setupStoredEntry(entry);
        }
        try (FileInputStream inputStream = new FileInputStream(dependencyFile)){
            outputStream.putArchiveEntry(entry);
            IOUtils.copy(inputStream, outputStream);
            outputStream.closeArchiveEntry();
        }
        return indexName;
    }

    protected void copyFileToPackage(File file, String rootDir) throws Exception {
        if(CommonUtils.isEmpty(rootDir)){
            rootDir = file.getName();
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            rootDir = rootDir + "/";
            putDirEntry(rootDir);
            if(childFiles == null){
                return;
            }
            for (File childFile : childFiles) {
                copyFileToPackage(childFile, rootDir + childFile.getName());
            }
        } else {
            putFileEntry(file, rootDir);
        }
    }

    protected void putFileEntry(File destFile, String rootDir) throws Exception {
        if(!destFile.exists()){
            throw new FileNotFoundException("Not found file : " + destFile.getPath());
        }
        outputStream.putArchiveEntry(getArchiveEntry(rootDir));
        FileUtils.copyFile(destFile, outputStream);
        outputStream.closeArchiveEntry();
    }

    protected void putInputStreamEntry(InputStream inputStream, String name) throws Exception {
        outputStream.putArchiveEntry(getArchiveEntry(name));
        IOUtils.copy(inputStream, outputStream);
        outputStream.closeArchiveEntry();
    }

    protected void putDirEntry(String dir) throws IOException {
        outputStream.putArchiveEntry(getArchiveEntry(dir));
        outputStream.closeArchiveEntry();
    }

    protected ZipArchiveEntry getArchiveEntry(String name){
        return new ZipArchiveEntry(name);
    }

    private static class CrcAndSize {

        private static final int BUFFER_SIZE = 32 * 1024;

        private final CRC32 crc = new CRC32();

        private long size;

        CrcAndSize(InputStream inputStream) throws IOException {
            load(inputStream);
        }

        private void load(InputStream inputStream) throws IOException {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                this.crc.update(buffer, 0, bytesRead);
                this.size += bytesRead;
            }
        }

        void setupStoredEntry(ZipArchiveEntry entry) {
            entry.setSize(this.size);
            entry.setCompressedSize(this.size);
            entry.setCrc(this.crc.getValue());
            entry.setMethod(ZipEntry.STORED);
        }

    }

}
