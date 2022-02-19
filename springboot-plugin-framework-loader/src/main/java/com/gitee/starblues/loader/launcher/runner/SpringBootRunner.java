package com.gitee.starblues.loader.launcher.runner;

import java.util.concurrent.CountDownLatch;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class SpringBootRunner implements Runnable{

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private final MethodRunner methodRunner;

    public SpringBootRunner(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    public void run() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            methodRunner.run(contextClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            COUNT_DOWN_LATCH.countDown();
        }
    }
}
