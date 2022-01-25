package com.gitee.starblues.plugin.pack;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author starBlues
 * @version 1.0
 */
public interface Repackager {


    void repackage() throws MojoExecutionException, MojoFailureException;


}
