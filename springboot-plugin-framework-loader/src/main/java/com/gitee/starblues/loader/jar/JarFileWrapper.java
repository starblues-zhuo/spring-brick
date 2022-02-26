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


import com.gitee.starblues.loader.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import com.gitee.starblues.loader.utils.ObjectUtils;

/**
 * copy from spring-boot-loader
 * @author starBlues
 * @version 3.0.0
 */
public class JarFileWrapper extends AbstractJarFile {

    private final String parentName;

    private final JarFile parent;

    private final Map<String, List<InputStream>> inputStreamCache;

    private final AtomicBoolean canClosed = new AtomicBoolean(false);

    JarFileWrapper(JarFile parent) throws IOException {
        super(parent.getRootJarFile().getFile());
        this.parent = parent;
        this.parentName = UUID.randomUUID().toString() + parent.getName();
        this.inputStreamCache = new ConcurrentHashMap<>();
    }

    @Override
    URL getUrl() throws MalformedURLException {
        return this.parent.getUrl();
    }

    @Override
    JarFileType getType() {
        return this.parent.getType();
    }

    @Override
    Permission getPermission() {
        return this.parent.getPermission();
    }

    @Override
    public Manifest getManifest() throws IOException {
        return this.parent.getManifest();
    }

    @Override
    public Enumeration<java.util.jar.JarEntry> entries() {
        return this.parent.entries();
    }

    @Override
    public Stream<java.util.jar.JarEntry> stream() {
        return this.parent.stream();
    }

    @Override
    public JarEntry getJarEntry(String name) {
        return this.parent.getJarEntry(name);
    }

    @Override
    public ZipEntry getEntry(String name) {
        return this.parent.getEntry(name);
    }

    @Override
    InputStream getInputStream() throws IOException {
        InputStream inputStream = this.parent.getInputStream();
        addInputStream(parentName, inputStream);
        return inputStream;
    }

    @Override
    public synchronized InputStream getInputStream(ZipEntry ze) throws IOException {
        InputStream inputStream = this.parent.getInputStream(ze);
        addInputStream(ze.getName(), inputStream);
        return inputStream;
    }

    @Override
    public String getComment() {
        return this.parent.getComment();
    }

    @Override
    public int size() {
        return this.parent.size();
    }

    @Override
    public String toString() {
        return this.parent.toString();
    }

    @Override
    public String getName() {
        return this.parent.getName();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if(canClosed.get()){
            for (List<InputStream> inputStreams : inputStreamCache.values()) {
                if(ObjectUtils.isEmpty(inputStreams)){
                    continue;
                }
                for (InputStream inputStream : inputStreams) {
                    if(inputStream == null){
                        continue;
                    }
                    IOUtils.closeQuietly(inputStream);
                }
            }
            parent.close();
        }
    }

    public void canClosed(){
        canClosed.set(true);
    }

    private void addInputStream(String name, InputStream inputStream){
        if(inputStream != null){
            List<InputStream> inputStreams = inputStreamCache.computeIfAbsent(name, k -> new ArrayList<>());
            inputStreams.add(inputStream);
        }
    }

    static JarFile unwrap(java.util.jar.JarFile jarFile) {
        if (jarFile instanceof JarFile) {
            return (JarFile) jarFile;
        }
        if (jarFile instanceof JarFileWrapper) {
            return unwrap(((JarFileWrapper) jarFile).parent);
        }
        throw new IllegalStateException("Not a JarFile or Wrapper");
    }

}

