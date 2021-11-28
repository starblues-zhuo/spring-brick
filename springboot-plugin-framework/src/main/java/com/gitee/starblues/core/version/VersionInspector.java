package com.gitee.starblues.core.version;

/**
 * 版本检查
 * @author starBlues
 * @version 3.0.0
 */
public interface VersionInspector {


    /**
     * 比较 v1 和 v2版本.
     * @param v1 版本号码1
     * @param v2 版本号码2
     * @return 如果 v1大于等于v2, 则返回大于等于0的数字, 否则返回小于0的数字
     */
    int compareTo(String v1, String v2);

}
