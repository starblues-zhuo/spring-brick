package com.gitee.starblues.core.launcher;

/**
 * @author starBlues
 * @version 1.0
 */
public class MainMethodRunner extends MethodRunner{

    public MainMethodRunner(String mainClass, String mainRunMethod, String[] args) {
        super(mainClass, mainRunMethod, args);
    }

    @Override
    protected Object getInstance(Class<?> mainClass) throws Exception {
        return null;
    }
}
