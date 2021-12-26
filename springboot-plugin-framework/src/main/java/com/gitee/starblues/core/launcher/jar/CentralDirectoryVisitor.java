package com.gitee.starblues.core.launcher.jar;

/**
 * @author starBlues
 * @version 1.0
 */
public interface CentralDirectoryVisitor {

    void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData);

    void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset);

    void visitEnd();

}
