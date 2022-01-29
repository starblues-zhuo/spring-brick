/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.core.launcher;

import com.gitee.starblues.utils.Assert;

import java.util.concurrent.CountDownLatch;

/**
 * 主程序引导器
 * @author starBlues
 * @version 3.0.0
 */
public class SpringMainBootstrap {

    private static final String MAIN_RUN_METHOD = "main";

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private static SpringBootstrap springBootstrap;

    public static void launch(Class<? extends SpringBootstrap> bootstrapClass, String[] args) {
        try {
            SpringBootstrap springBootstrap = bootstrapClass.getConstructor().newInstance();
            launch(springBootstrap, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launch(SpringBootstrap springBootstrap, String[] args) {
        SpringMainBootstrap.springBootstrap = Assert.isNotNull(springBootstrap, "springBootBootstrap 不能为空");
        MainMethodRunner mainMethodRunner = new MainMethodRunner(SpringMainBootstrap.class.getName(),
                MAIN_RUN_METHOD, args);
        Thread launchThread = new Thread(new Runner(mainMethodRunner));
        launchThread.start();
        try {
            COUNT_DOWN_LATCH.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void main(String[] args) throws Exception {
        Assert.isNotNull(springBootstrap, "springBootstrap 不能为空");
        MainProgramLauncher launcher = new MainProgramLauncher(springBootstrap);
        launcher.run(args);
    }


    private static class Runner implements Runnable{

        private final MainMethodRunner mainMethodRunner;

        private Runner(MainMethodRunner mainMethodRunner) {
            this.mainMethodRunner = mainMethodRunner;
        }

        @Override
        public void run() {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                mainMethodRunner.run(contextClassLoader);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                COUNT_DOWN_LATCH.countDown();
            }
        }
    }

}
