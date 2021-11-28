package com.gitee.starblues.core.scanner;

import com.gitee.starblues.utils.ObjectUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 生产环境目录解决器
 * @author starBlues
 * @version 3.0.0
 */
public class ProdPathResolve implements PathResolve{

    private final List<String> extensionNames = new ArrayList<>();

    public ProdPathResolve(){
        addExtensionName(".jar");
    }

    protected void addExtensionName(String extensionName){
        if(ObjectUtils.isEmpty(extensionName)){
            return;
        }
        // jar包
        extensionNames.add(extensionName);
    }

    @Override
    public Path resolve(Path path) {
        String fileName = path.getFileName().toString();
        for (String extensionName : extensionNames) {
            boolean exist = fileName.toLowerCase().endsWith(extensionName.toUpperCase());
            if(exist){
                return path;
            }
        }
        return null;
    }
}
