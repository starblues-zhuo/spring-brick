package com.gitee.starblues.plugin.pack;

/**
 * @author starBlues
 * @version 1.0
 */
public class Constant {

    public static final String PACKAGING_POM = "pom";
    public static final String SCOPE_PROVIDED = "provided";

    public static final String MODE_DEV = "dev";
    public static final String MODE_PROD = "prod";

    public static final String PACKAGE_TYPE_JAR = "jar";
    public static final String PACKAGE_TYPE_ZIP = "zip";
    public static final String PACKAGE_TYPE_DIR = "dir";


    public static boolean isPom(String packageType){
        return PACKAGING_POM.equalsIgnoreCase(packageType);
    }

}
