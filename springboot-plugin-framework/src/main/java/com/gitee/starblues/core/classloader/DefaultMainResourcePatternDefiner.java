package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * 默认主程序资源定义者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultMainResourcePatternDefiner implements MainResourcePatternDefiner {

    private final Set<String> resourcePatterns = new HashSet<>();

    private final String mainPackageName;

    public DefaultMainResourcePatternDefiner(String mainPackageName){
        this.mainPackageName = Assert.isNotEmpty(mainPackageName,
                "参数 mainPackageName 不能为空");
        initDefaultResources();
    }


    @Override
    public Set<String> getIncludePatterns() {
        return resourcePatterns;
    }

    @Override
    public Set<String> getExcludePatterns() {
        return null;
    }


    private void initDefaultResources() {
        // == java ==
        resourcePatterns.add("java/**");
        resourcePatterns.add("javax/**");
        resourcePatterns.add("sun/**");

        // == main ==
        resourcePatterns.add(mainPackageName.replace(".", "/"));

        // == springboot-plugin-framework ==
        resourcePatterns.add("com/gitee/starblues/**");

        // aop
        resourcePatterns.add("org/aspectj/**");

        // === spring-boot-starter ===
        // logback-classic
        resourcePatterns.add("ch/qos/logback/classic/**");
        resourcePatterns.add("org/slf4j/impl/**");
        // logback-core
        resourcePatterns.add("ch/qos/logback/core/**");
        // log4j-api
        resourcePatterns.add("org/apache/logging/log4j/**");
        // log4j-to-slf4j
        resourcePatterns.add("org/apache/logging/slf4j/**");
        // slf4j-api jul-to-slf4j(org/slf4j/bridge/**)
        resourcePatterns.add("org/slf4j/**");

        resourcePatterns.add("META-INF/spring.factories");

        // spring-boot
        resourcePatterns.add("org/springframework/boot/**");
        // spring-boot-autoconfigure
        resourcePatterns.add("org/springframework/boot/autoconfigure/**");
        // spring-core spring-context spring-aop spring-beans
        resourcePatterns.add("org/aopalliance/**");
        resourcePatterns.add("org/springframework/aop/**");
        // spring-beans spring-context spring-core spring-expression
        resourcePatterns.add("org/springframework/**");
        // spring-jcl
        resourcePatterns.add("org/apache.commons/logging/**");
        // snakeyaml
        resourcePatterns.add("org/yaml/snakeyaml/**");
    }


}
