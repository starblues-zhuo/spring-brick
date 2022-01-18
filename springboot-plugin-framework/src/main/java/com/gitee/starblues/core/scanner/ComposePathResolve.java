package com.gitee.starblues.core.scanner;

import com.gitee.starblues.utils.ObjectUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 组合的PathResolve
 * @author starBlues
 * @version 1.0
 */
public class ComposePathResolve implements PathResolve{

    private final List<PathResolve> pathResolves;

    public ComposePathResolve(PathResolve ...pathResolves) {
        this(Arrays.asList(pathResolves));
    }

    public ComposePathResolve(List<PathResolve> pathResolves) {
        if(ObjectUtils.isEmpty(pathResolves)){
            this.pathResolves = new ArrayList<>();
        } else {
            this.pathResolves = pathResolves;
        }
    }

    public void addPathResolve(PathResolve pathResolve){
        if(pathResolve != null){
            pathResolves.add(pathResolve);
        }
    }

    @Override
    public Path resolve(Path path) {
        for (PathResolve pathResolve : pathResolves) {
            Path resolvePath = pathResolve.resolve(path);
            if(resolvePath != null){
                return resolvePath;
            }
        }
        return null;
    }
}
