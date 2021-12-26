package com.gitee.starblues.core.launcher;

/**
 * @author starBlues
 * @version 1.0
 */
public interface SpringBootstrap {

    String RUN_METHOD_NAME = "run";


    void run(String[] args) throws Exception;

}
