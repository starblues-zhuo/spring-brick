package com.gitee.starblues.core.version;

import com.github.zafarkhaja.semver.Version;

/**
 * Semver标准版本检查
 * @author starBlues
 * @version 3.0.0
 */
public class SemverVersionInspector implements VersionInspector{

    @Override
    public int compareTo(String version1, String version2) {
        Version v1 = Version.valueOf(version1);
        Version v2 = Version.valueOf(version2);
        return v1.compareTo(v2);
    }

}
