package com.gitee.starblues.plugin.pack.prod;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author starBlues
 * @version 1.0
 */
public interface JarWriter {

    void writeEntry(String name, InputStream inputStream) throws IOException;

}
