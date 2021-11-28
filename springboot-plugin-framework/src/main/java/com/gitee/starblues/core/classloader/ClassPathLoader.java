package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

/**
 * classpath 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ClassPathLoader extends AbstractResourceLoader{

    private final URL url;

    public ClassPathLoader(URL url) {
        this.url = Assert.isNotNull(url, "url 不能为空");
    }

    @Override
    public void init() throws Exception {
        File file = new File(url.toURI());
        load(file, "");
    }

    private void load(File file, String packageName) throws Exception {
        if(file.isFile()){
            // 文件
            loadResource(file, packageName);
            return;
        }
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            if(listFiles == null || listFiles.length == 0){
                return;
            }
            for (File subFile : listFiles) {
                String currentPackage = packageName;
                if (subFile.isDirectory()) {
                    if (!ObjectUtils.isEmpty(currentPackage)) {
                        currentPackage = currentPackage + "/";
                    }
                    currentPackage = currentPackage + subFile.getName();
                }
                load(subFile, currentPackage);
            }
        }
    }

    private void loadResource(File file, String packageName) throws Exception{
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];

            if (fileInputStream.read(bytes) != -1) {
                String name;
                String fileName = file.getName();
                if(!ObjectUtils.isEmpty(packageName)){
                    name = packageName + "/" + fileName;
                } else {
                    name = fileName;
                }
                Resource resource = new Resource(
                        name, url, new URL(url.toString() + name), bytes
                );
                addResource(name, resource);
            }
        }

    }
}
