package com.gitee.starblues.core.scanner;

import com.gitee.starblues.common.PackageStructure;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 开发环境路径解决器
 * @author starBlues
 * @version 3.0.0
 */
public class DevPathResolve implements PathResolve{

    private final List<String> devCompilePackageNames = new ArrayList<>();

    public DevPathResolve() {
        addCompilePackageName();
    }

    protected void addCompilePackageName(){
        // 添加插件信息查询目录
        devCompilePackageNames.add("target".concat(File.separator).concat(PackageStructure.META_INF_NAME));
    }

    @Override
    public Path resolve(Path path) {
        for (String devCompilePackageName : devCompilePackageNames) {
            String compilePackagePathStr = path.toString() + File.separator + devCompilePackageName;
            Path compilePackagePath = Paths.get(compilePackagePathStr);
            if(Files.exists(compilePackagePath)){
                return compilePackagePath;
            }
        }
        return null;
    }
}
