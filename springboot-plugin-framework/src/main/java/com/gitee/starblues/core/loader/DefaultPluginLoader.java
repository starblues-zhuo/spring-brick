package com.gitee.starblues.core.loader;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.classloader.MainResourceDefiner;
import com.gitee.starblues.core.classloader.PluginClassLoader;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.spring.BasePluginSpringApplication;
import com.gitee.starblues.core.spring.PluginSpringApplication;
import com.gitee.starblues.core.spring.environment.PluginLocalConfigFileProcessor;
import com.gitee.starblues.utils.Assert;
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

    private final MainResourceDefiner mainResourceDefiner;

    private final List<WeakReference<PluginClassLoader>> classLoaderCache = new ArrayList<>();

    public DefaultPluginLoader(MainResourceDefiner mainResourceDefiner) {
        this.mainResourceDefiner = mainResourceDefiner;
    }

    @Override
    public PluginWrapperInside load(PluginDescriptor descriptor) throws Exception {
        Assert.isNotNull(descriptor, "参数 PluginDescriptor 不能为空");
        PluginClassLoader classLoader = getClassLoader(descriptor);
        installLib(classLoader, descriptor.getPluginLibDir());
        Class<?> bootstrapClass = classLoader.loadClass(descriptor.getPluginClass());
        PluginSpringApplication springApplication = getPluginSpringApplication(descriptor,
                classLoader, bootstrapClass);
        PluginWrapperInside pluginWrapperInside = new PluginWrapperInside(
                descriptor.getPluginId(),
                descriptor,
                classLoader,
                bootstrapClass,
                descriptor.getPluginPath(),
                springApplication.getApplicationContext()
        );
        pluginWrapperInside.setPluginState(PluginState.CREATED);
        return pluginWrapperInside;
    }

    private PluginSpringApplication getPluginSpringApplication(PluginDescriptor descriptor,
                                                               ClassLoader classLoader,
                                                               Class<?> bootstrapClass) {
        return new BasePluginSpringApplication(classLoader, bootstrapClass, descriptor.getConfigFileName());
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
                    mainResourceDefiner);
            classLoaderCache.add(new WeakReference<>(pnClassLoader));
        }
        return pnClassLoader;
    }

    protected void installLib(PluginClassLoader classLoader, String lib){
        if(ObjectUtils.isEmpty(lib)){
            return;
        }
        File file = new File(lib);
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
