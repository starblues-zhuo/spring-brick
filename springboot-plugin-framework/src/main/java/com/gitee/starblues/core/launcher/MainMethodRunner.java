package com.gitee.starblues.core.launcher;

/**
 * 主程序方法启动者
 * @author starBlues
 * @version 3.0.0
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
