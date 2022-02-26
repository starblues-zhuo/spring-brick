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

package com.gitee.starblues.loader.jar;

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
