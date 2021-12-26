package com.gitee.starblues.core.loader;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;
import com.gitee.starblues.core.classloader.PluginClassLoader;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 默认的插件加载者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginLoader implements PluginLoader{

    private final MainResourcePatternDefiner mainResourcePatternDefiner;

    private final List<WeakReference<PluginClassLoader>> classLoaderCache = new ArrayList<>();

    public DefaultPluginLoader(MainResourcePatternDefiner mainResourcePatternDefiner) {
        this.mainResourcePatternDefiner = Assert.isNotNull(mainResourcePatternDefiner,
                "参数 mainResourcePatternDefiner 不能为空");
    }

    @Override
    public PluginWrapperInside load(PluginDescriptor descriptor) throws Exception {
        Assert.isNotNull(descriptor, "参数 PluginDescriptor 不能为空");
        PluginClassLoader classLoader = getClassLoader(descriptor);
        installLib(classLoader, descriptor);
        Class<?> bootstrapClass = classLoader.loadClass(descriptor.getPluginClass());
        PluginWrapperInside pluginWrapperInside = new PluginWrapperInside(
                descriptor.getPluginId(),
                descriptor,
                classLoader,
                bootstrapClass,
                descriptor.getPluginPath()
        );
        pluginWrapperInside.setPluginState(PluginState.CREATED);
        return pluginWrapperInside;
    }

    protected synchronized PluginClassLoader getClassLoader(PluginDescriptor descriptor){
        String pluginId = descriptor.getPluginId();
        Path classPath = descriptor.getPluginPath();
        Iterator<WeakReference<PluginClassLoader>> iterator = classLoaderCache.iterator();
        PluginClassLoader pnClassLoader = null;
        while (iterator.hasNext()){
            WeakReference<PluginClassLoader> weakReference = iterator.next();
            PluginClassLoader cacheClassLoader = weakReference.get();
            if(cacheClassLoader == null){
                iterator.remove();
                continue;
            }
            if(Objects.equals(pluginId, cacheClassLoader.getPluginId())){
                pnClassLoader = cacheClassLoader;
                pnClassLoader.addResource(classPath.toFile());
            }
        }
        if(pnClassLoader == null){
            pnClassLoader = new PluginClassLoader(pluginId, classPath, getParentClassLoader(),
                    mainResourcePatternDefiner);
            classLoaderCache.add(new WeakReference<>(pnClassLoader));
        }
        return pnClassLoader;
    }

    protected void installLib(PluginClassLoader classLoader, PluginDescriptor descriptor){
        String pluginLibDir = descriptor.getPluginLibDir();

        if(ObjectUtils.isEmpty(pluginLibDir)){
            return;
        }
        File file = getExistLibFile(descriptor);
        if(file == null){
            return;
        }
        addJarFile(classLoader, file);
        if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files == null || files.length == 0){
                return;
            }
            for (File subFile : files) {
                addJarFile(classLoader, subFile);
            }
        }
    }

    private File getExistLibFile(PluginDescriptor descriptor){
        String pluginLibDir = descriptor.getPluginLibDir();
        File file = new File(pluginLibDir);
        if(file.exists()){
            return file;
        }
        String relativePath = CommonUtils.joiningFilePath(descriptor.getPluginPath().toString(), pluginLibDir);
        file = new File(relativePath);
        if(file.exists()){
            return file;
        }
        return null;
    }


    private void addJarFile(PluginClassLoader pnClassLoader, File file){
        if(!file.exists()){
            return;
        }
        if(PluginFileUtils.isJarFile(file.toPath())){
            pnClassLoader.addResource(file);
        }
    }

    protected ClassLoader getParentClassLoader(){
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 忽略
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 忽略
                }
            }
        }
        return cl;
    }

}
