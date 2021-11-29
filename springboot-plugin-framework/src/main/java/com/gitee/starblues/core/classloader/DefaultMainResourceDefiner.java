package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.Assert;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 默认主程序资源定义者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultMainResourceDefiner implements MainResourceDefiner{

    private final Set<String> mainClass = new HashSet<>();
    private final Set<String> mainResources = new HashSet<>();
    private final Set<String> springFactories = new HashSet<>();

    private final String mainPackageName;

    public DefaultMainResourceDefiner(String mainPackageName){
        this.mainPackageName = Assert.isNotEmpty(mainPackageName,
                "参数 mainPackageName 不能为空");
        initDefaultClass(mainPackageName);
        initDefaultResources(mainPackageName);
        initDefaultSpringFactories();
    }


    @Override
    public Set<String> getClassNames() {
        return mainClass;
    }

    @Override
    public Set<String> getResources() {
        return mainResources;
    }

    @Override
    public Set<String> getSpringFactories() {
        return springFactories;
    }

    protected void initDefaultClass(String mainPackageName) {
        // == java ==
        mainClass.add("java.");
        mainClass.add("javax.");

        // == main ==
        mainClass.add(mainPackageName);

        // == springboot-plugin-framework ==
        mainClass.add("com.gitee.starblues");

        // === spring-boot-starter ===
        // logback-classic
        mainClass.add("ch.qos.logback.classic");
        mainClass.add("org.slf4j.impl");
        // logback-core
        mainClass.add("ch.qos.logback.core");
        // log4j-api
        mainClass.add("org.apache.logging.log4j");
        // log4j-to-slf4j
        mainClass.add("org.apache.logging.slf4j");
        // jul-to-slf4j
        mainClass.add("org.slf4j.bridge");
        // slf4j-api
        mainClass.add("org.slf4j");

        // spring-boot
        mainClass.add("org.springframework.boot");
        // spring-boot-autoconfigure
        mainClass.add("org.springframework.boot.autoconfigure");
        // spring-core spring-context spring-aop spring-beans
        mainClass.add("org.aopalliance");
        mainClass.add("org.springframework.aop");
        // spring-beans spring-context spring-core spring-expression
        mainClass.add("org.springframework");
        // spring-jcl
        mainClass.add("org.apache.commons.logging");
        // snakeyaml
        mainClass.add("org.yaml.snakeyaml");
    }

    private void initDefaultResources(String mainPackageName) {
        mainResources.add(mainPackageName.replace(".", "/"));
        mainResources.add("com/gitee/starblues");
        mainResources.add("org/springframework");
        mainResources.add("org/slf4j/Logger");
        mainResources.add("org/aopalliance");
        mainResources.add(SpringFactoriesLoader.FACTORIES_RESOURCE_LOCATION);
    }

    private void initDefaultSpringFactories() {
        springFactories.add("/spring-boot/");
        springFactories.add("/spring-beans/");
        springFactories.add("/springboot-plugin-framework/");
        // 当前框架名称
        springFactories.add(mainPackageName);
    }
}
