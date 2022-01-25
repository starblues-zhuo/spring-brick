package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * @author starBlues
 * @version 1.0
 */
public class ProdPluginDescriptorLoader implements PluginDescriptorLoader{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PluginDescriptorLoader target;

    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        if(ResourceUtils.isJarFile(location)){
            target = new ProdPackagePluginDescriptorLoader(PluginDescriptor.Type.JAR);
        } else if(ResourceUtils.isZipFile(location)){
            target = new ProdPackagePluginDescriptorLoader(PluginDescriptor.Type.ZIP);
        } else if(ResourceUtils.isDirFile(location)){
            target = new ProdDirPluginDescriptorLoader();
        } else {
            logger.warn("不能解析文件: {}", location);
            return null;
        }
        return target.load(location);
    }

    @Override
    public void close() throws Exception {
        target.close();
    }
}
