package com.gitee.starblues.plugin.pack.prod;

import org.apache.maven.plugin.MojoFailureException;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PackageGenerator {

    void pack() throws MojoFailureException;

}
