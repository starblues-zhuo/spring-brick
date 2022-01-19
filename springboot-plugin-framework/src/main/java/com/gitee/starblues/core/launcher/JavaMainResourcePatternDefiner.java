package com.gitee.starblues.core.launcher;

import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;

import java.util.HashSet;
import java.util.Set;

/**
 * java 内部包匹配定义
 * @author starBlues
 * @version 3.0.0
 */
public class JavaMainResourcePatternDefiner implements MainResourcePatternDefiner {

    protected final Set<String> includes = new HashSet<>();

    public JavaMainResourcePatternDefiner(){
        // java 内部的包匹配定义
        addJava();
        addJavax();
        addSun();
        addJdk();
        addJavaXml();
    }

    protected void addJava(){
        includes.add("java/**");
    }

    protected void addJavax(){
        includes.add("javax/**");
        includes.add("org/ietf/jgss/**");
    }

    protected void addJdk(){
        includes.add("jdk/**");
        // jdk.internal.vm.compiler
        includes.add("org/graalvm/**");
        // jdk.internal.vm.compiler.management
        includes.add("org/graalvm/compiler/hotspot/management/**");
        // jdk.hotspot.agent
        includes.add("images/toolbarButtonGraphics/general/**");
        includes.add("toolbarButtonGraphics/**");
    }

    protected void addJavaXml(){
        // jdk.xml.dom
        includes.add("org/w3c/dom/**");
        includes.add("org/xml/sax/**");
        includes.add("org/jcp/xml/dsig/internal**");
    }

    protected void addSun(){
        includes.add("com/sun/**");
        includes.add("sun/**");
    }

    @Override
    public Set<String> getIncludePatterns() {
        return includes;
    }

    @Override
    public Set<String> getExcludePatterns() {
        return null;
    }
}
