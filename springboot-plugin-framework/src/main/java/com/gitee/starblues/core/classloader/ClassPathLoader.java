package com.gitee.starblues.core.classloader;

import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Set;

/**
 * classpath 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ClassPathLoader extends AbstractResourceLoader{

    private final URL url;

    public ClassPathLoader(URL url) {
        super(url);
        this.url = Assert.isNotNull(url, "url 不能为空");
    }

    @Override
    public void init() throws Exception {
        File file = new File(url.toURI());
        load(file, null);
    }

    private void load(File file, String currentPackageName) throws Exception {
        if(currentPackageName == null){
            // 根目录
            currentPackageName = "";
        } else {
            if("".equals(currentPackageName)){
                currentPackageName = file.getName();
            } else {
                currentPackageName = currentPackageName + ResourceUtils.PACKAGE_SPLIT + file.getName();
            }
            loadResource(file, currentPackageName);
        }
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            if(listFiles == null || listFiles.length == 0){
                return;
            }
            for (File subFile : listFiles) {
                load(subFile, currentPackageName);
            }
        }
    }

    private void loadResource(File file, String packageName) throws Exception{
        if(file.isDirectory()){
            addResource(file, packageName + ResourceUtils.PACKAGE_SPLIT, null);
            return;
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];

            if (fileInputStream.read(bytes) != -1) {
                addResource(file, packageName, bytes);
            }
        }
    }

    private void addResource(File file, String packageName, byte[] bytes) throws MalformedURLException {
        Resource resource = new Resource(
                file.getName(), url, new URL(url.toString() + packageName), bytes
        );
        addResource(packageName, resource);
    }

}
