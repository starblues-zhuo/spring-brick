package com.gitee.starblues.core.scanner;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 生产环境目录解决器
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPathResolve implements PathResolve{

    private final List<String> packageSuffix = new ArrayList<>();

    public ProdPathResolve(){
        // jar包
        addPackageSuffix(".jar");
        // zip包
        addPackageSuffix(".zip");
    }

    protected void addPackageSuffix(String name){
        if(ObjectUtils.isEmpty(name)){
            return;
        }
        packageSuffix.add(name);
    }

    @Override
    public Path resolve(Path path) {
        if(isDirPlugin(path)){
            return path;
        }
        String fileName = path.getFileName().toString().toLowerCase();
        for (String suffixName : packageSuffix) {
            boolean exist = fileName.endsWith(suffixName.toLowerCase());
            if(exist){
                return path;
            }
        }
        return null;
    }

    protected boolean isDirPlugin(Path path){
        File file = new File(CommonUtils.joiningFilePath(path.toString(), PackageStructure.resolvePath(
                PackageStructure.PROD_MANIFEST_PATH
        )));
        return file.exists() && file.isFile();
    }
}
