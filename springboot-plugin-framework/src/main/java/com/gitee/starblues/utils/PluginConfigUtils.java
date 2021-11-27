package com.gitee.starblues.utils;

import com.gitee.starblues.annotation.ConfigDefinition;
import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;

/**
 * 插件配置工具类
 * @author starBlues
 * @version 2.4.3
 */
public class PluginConfigUtils {

    private static final String DO = "-";

    private PluginConfigUtils(){}

    /**
     * 根据项目运行环境模式来获取配置文件名称
     * @param fileName 文件名称
     * @param prodSuffix 生产环境前缀
     * @param devSuffix 开发环境前缀
     * @param runtimeMode 运行模式
     * @return 文件名称
     */
    public static FileNamePack getConfigFileName(String fileName,
                                                 String prodSuffix,
                                                 String devSuffix,
                                                 RuntimeMode runtimeMode){
        if(StringUtils.isNullOrEmpty(fileName)){
            return null;
        }
        String suffix = "";
        if(runtimeMode == RuntimeMode.DEPLOYMENT){
            // 生产环境
            suffix = prodSuffix;
        } else if(runtimeMode == RuntimeMode.DEVELOPMENT){
            // 开发环境
            suffix = devSuffix;
        }

        return new FileNamePack(fileName, suffix);
    }

    public static String joinConfigFileName(FileNamePack fileNamePack){
        if(fileNamePack == null){
            return null;
        }
        return joinConfigFileName(fileNamePack.getSourceFileName(), fileNamePack.getFileSuffix());
    }

    public static String joinConfigFileName(String fileName, String suffix){
        if(StringUtils.isNullOrEmpty(fileName)){
            return null;
        }
        String fileNamePrefix;
        String fileNamePrefixSuffix;

        if(fileName.lastIndexOf(".") == -1) {
            fileNamePrefix = fileName;
            fileNamePrefixSuffix = "";
        } else {
            int index = fileName.lastIndexOf(".");
            fileNamePrefix = fileName.substring(0, index);
            fileNamePrefixSuffix = fileName.substring(index);
        }
        if(suffix == null){
            suffix = "";
        }
        if(StringUtils.isNotNullOrEmpty(suffix) && !suffix.startsWith(DO)){
            suffix = DO + suffix;
        }
        return fileNamePrefix + suffix + fileNamePrefixSuffix;
    }


    public static class FileNamePack {
        private final String sourceFileName;
        private final String fileSuffix;

        public FileNamePack(String sourceFileName, String fileSuffix) {
            this.sourceFileName = sourceFileName;
            this.fileSuffix = fileSuffix;
        }

        public String getSourceFileName() {
            return sourceFileName;
        }

        public String getFileSuffix() {
            return fileSuffix;
        }
    }

}
