package com.gitee.starblues.core.launcher.jar;

/**
 * copy from spring-boot-loader
 * @author starBlues
 * @version 3.0.0
 */
public interface CentralDirectoryVisitor {

    /**
     * visitStart
     * @param endRecord endRecord
     * @param centralDirectoryData centralDirectoryData
     */
    void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData);

    /**
     * visitFileHeader
     * @param fileHeader fileHeader
     * @param dataOffset dataOffset
     */
    void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset);

    /**
     * visitEnd
     */
    void visitEnd();

}
