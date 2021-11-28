package com.gitee.starblues.core.scanner;

import java.nio.file.Path;

/**
 * 目录解决器
 * @author starBlues
 * @version 3.0.0
 */
public interface PathResolve {

    /**
     * 过滤并返回正确的路径
     * @param path 待过滤路径
     * @return  path 处理后的路径, 返回null 表示不可用
     */
    Path resolve(Path path);


}
