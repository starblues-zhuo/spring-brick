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

package com.gitee.starblues.plugin.pack.utils;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ObjectUtils;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import static com.gitee.starblues.common.PackageStructure.*;

/**
 * zip 打包工具
 * @author starBlues
 * @version 3.0.0
 */
public class PackageZip implements Closeable{


    private static final int UNIX_FILE_MODE = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

    private static final int UNIX_DIR_MODE = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

    private final File file;
    private final ArchiveOutputStream outputStream;


    public PackageZip(File file) throws Exception {
        this.file = file;
        this.outputStream = getOutputStream(file);
    }

    public PackageZip(String outputDirectory, String packageName) throws Exception{
        String rootPath = FilesUtils.joiningFilePath(outputDirectory, packageName);
        this.file = getPackageFile(rootPath);
        this.outputStream = getOutputStream(file);
    }

    public File getFile(){
        return file;
    }

    public String getFileName(){
        return file.getName();
    }

    protected File getPackageFile(String rootPath) throws Exception {
        String fileSuffix = getPackageFileSuffix();
        File file = new File(rootPath + "." + fileSuffix);

        if(file.exists()){
            int i = 0;
            while (true){
                file = new File(rootPath + "_" + i + "." + fileSuffix);
                if(file.exists()){
                    i = i + 1;
                    continue;
                }
                break;
            }
        }
        if(file.createNewFile()){
            return file;
        }
        throw new IOException("Create file '" + file.getPath() + "' failure.");
    }

    protected String getPackageFileSuffix(){
        return "zip";
    }

    protected ArchiveOutputStream getOutputStream(File packFile) throws Exception {
        return new ZipArchiveOutputStream(new FileOutputStream(packFile));
    }

    public void copyDirToPackage(File rootDir, String packageDir) throws Exception {
        if(packageDir == null){
            packageDir = rootDir.getName();
        }
        if (rootDir.isDirectory()) {
            File[] childFiles = rootDir.listFiles();
            if(ObjectUtils.isEmpty(packageDir)){
                packageDir = "";
            } else {
                packageDir = packageDir + "/";
                putDirEntry(packageDir);
            }
            if(childFiles == null){
                return;
            }
            for (File childFile : childFiles) {
                copyDirToPackage(childFile, packageDir + childFile.getName());
            }
        } else {
            putFileEntry(rootDir, packageDir);
        }
    }

    public void copyZipToPackage(File sourceZipFile) throws Exception {
        if(sourceZipFile == null || !sourceZipFile.exists()){
            return;
        }
        try (ZipFile zipFile = new ZipFile(sourceZipFile)){
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()){
                ZipArchiveEntry zipArchiveEntry = entries.nextElement();
                String name = zipArchiveEntry.getName();
                if(name.contains(PackageStructure.META_INF_NAME)){
                    // 不拷贝 mate-inf
                    continue;
                }
                if(zipArchiveEntry.isDirectory()){
                    putDirEntry(name);
                } else {
                    try (InputStream inputStream = zipFile.getInputStream(zipArchiveEntry)){
                        putInputStreamEntry(name, inputStream);
                    }
                }
            }
        }
    }

    public String writeDependency(File dependencyFile, String libDirEntryName) throws Exception {
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

    public void putFileEntry(File destFile, String rootDir) throws Exception {
        if(!destFile.exists()){
            throw new FileNotFoundException("Not found file : " + destFile.getPath());
        }
        outputStream.putArchiveEntry(getArchiveEntry(rootDir));
        FileUtils.copyFile(destFile, outputStream);
        outputStream.closeArchiveEntry();
    }

    public void putInputStreamEntry(String name, InputStream inputStream) throws Exception {
        outputStream.putArchiveEntry(getArchiveEntry(name));
        IOUtils.copy(inputStream, outputStream);
        outputStream.closeArchiveEntry();
    }

    public void write(String name, Writer writer) throws Exception {
        outputStream.putArchiveEntry(getArchiveEntry(name));
        writer.write(outputStream);
        outputStream.closeArchiveEntry();
    }

    public void write(String name, File file) throws Exception {
        outputStream.putArchiveEntry(getArchiveEntry(name));
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            IOUtils.copy(fileInputStream, outputStream);
            outputStream.closeArchiveEntry();
        }
    }

    public void writeManifest(Manifest manifest) throws Exception {
        putDirEntry(META_INF_NAME + SEPARATOR);
        write(PROD_MANIFEST_PATH, manifest::write);
    }

    public void putDirEntry(String dir) throws IOException {
        outputStream.putArchiveEntry(getArchiveEntry(dir));
        outputStream.closeArchiveEntry();
    }

    protected ZipArchiveEntry getArchiveEntry(String name){
        return new ZipArchiveEntry(name);
    }


    @Override
    public void close() throws IOException {
        outputStream.finish();
        outputStream.close();
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

    @FunctionalInterface
    public interface Writer{
        void write(ArchiveOutputStream outputStream) throws Exception;
    }

}
