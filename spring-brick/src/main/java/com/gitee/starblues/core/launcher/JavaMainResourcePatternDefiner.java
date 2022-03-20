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
