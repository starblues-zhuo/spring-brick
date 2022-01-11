package com.gitee.starblues.plugin.pack.dev;

import com.gitee.starblues.plugin.pack.utils.CommonUtils;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author starBlues
 * @version 1.0
 */
public class LibCopy {

    private static final int UNIX_FILE_MODE = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

    private static final int UNIX_DIR_MODE = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

    private final String destDir;


    public LibCopy(String destDir) {
        this.destDir = destDir;
    }

    public void copy(String libPath) throws Exception{
        copy(new File(libPath));
    }

    public void copy(File libFile) throws Exception{
        if(libFile == null || !libFile.exists()){
            return;
        }
        File destJarFile = new File(CommonUtils.joinPath(destDir, libFile.getName()));
        if(!destJarFile.createNewFile()){
            return;
        }
        try (JarArchiveOutputStream outputStream = new JarArchiveOutputStream(new FileOutputStream(destJarFile));
             JarArchiveInputStream jarArchiveInputStream = new JarArchiveInputStream(new FileInputStream(libFile))) {
            JarArchiveEntry entry = null;
            while ((entry = jarArchiveInputStream.getNextJarEntry()) != null) {
                String name = entry.getName();
                JarArchiveEntry newEntry = new JarArchiveEntry(name);
                //copyAttribute(entry, newEntry);
                //CopyByte crcAndSize = new CopyByte(jarArchiveInputStream);
                //crcAndSize.setupStoredEntry(newEntry);
                entry.setMethod(ZipEntry.STORED);
                outputStream.putArchiveEntry(newEntry);
                IOUtils.copy(jarArchiveInputStream, outputStream);
                outputStream.closeArchiveEntry();
            }
            outputStream.finish();
        }
    }

    private void copyAttribute(JarArchiveEntry oldEntry, JarArchiveEntry newEntry){
        newEntry.setComment(oldEntry.getComment());
        newEntry.setTime(System.currentTimeMillis());
    }

    private static class CopyByte {

        private static final int BUFFER_SIZE = 32 * 1024;

        private final CRC32 crc = new CRC32();

        private long size;

        public CopyByte(InputStream inputStream) throws IOException{
            copy(inputStream);
        }

        void copy(InputStream inputStream) throws IOException {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                this.crc.update(buffer, 0, bytesRead);
                this.size += bytesRead;
            }
        }

        void setupStoredEntry(JarArchiveEntry entry) {
            entry.setSize(this.size);
            entry.setCompressedSize(this.size);
            entry.setCrc(this.crc.getValue());
            entry.setMethod(ZipEntry.STORED);
        }

    }



}
