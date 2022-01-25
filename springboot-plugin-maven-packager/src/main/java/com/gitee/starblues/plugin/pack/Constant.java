package com.gitee.starblues.plugin.pack;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class Constant {

    public static final String PACKAGING_POM = "pom";
    public static final String SCOPE_PROVIDED = "provided";
    public static final String SCOPE_COMPILE = "compile";
    public static final String SCOPE_TEST = "test";

    public static final String MODE_DEV = "dev";
    public static final String MODE_PROD = "prod";

    public static final String PACKAGE_TYPE_JAR = "jar";
    public static final String PACKAGE_TYPE_ZIP = "zip";
    public static final String PACKAGE_TYPE_DIR = "dir";


    public static boolean isPom(String packageType){
        return PACKAGING_POM.equalsIgnoreCase(packageType);
    }

    public static boolean scopeFilter(String scope){
        return SCOPE_PROVIDED.equalsIgnoreCase(scope)
                || SCOPE_TEST.equalsIgnoreCase(scope);
    }

}
