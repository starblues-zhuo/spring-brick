package com.gitee.starblues.extension.log.config;

import com.gitee.starblues.extension.log.annotation.ConfigItem;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 日志配置
 * @author sousouki
 * @version 2.4.3
 */
@XmlRootElement(name = "log")
public class LogConfig {

    public static final String ROOT_PLUGIN_SIGN = "~";

    /**
     * 日志存储根目录，默认为当前插件存放目录。
     * ~: 符号表示当前插件根目录
     **/
    @XmlElement(name = "rootDir")
    @ConfigItem(defaultValue = ROOT_PLUGIN_SIGN + "/logs/")
    private String rootDir;


    /**
     * 日志文件名称
     **/
    @XmlElement(name = "fileName")
    private String fileName;

    /**
     * 日志级别
     **/
    @XmlElement(name = "level")
    @ConfigItem(defaultValue = "INFO")
    private String level;

    /**
     * 日志文件最大容量
     **/
    @XmlElement(name = "maxFileSize")
    @ConfigItem(defaultValue = "10MB")
    private String maxFileSize;

    /**
     * 日志文件总容量
     **/
    @XmlElement(name = "totalFileSize")
    @ConfigItem(defaultValue = "10GB")
    private String totalFileSize;

    /**
     * 最大保存时间
     **/
    @XmlElement(name = "maxHistory")
    @ConfigItem(defaultValue = "30")
    private Integer maxHistory;

    /**
     * 日志内容格式
     **/
    @XmlElement(name = "pattern")
    @ConfigItem(defaultValue = "%d{yyyy-MM-dd HH:mm:ss.SSS} -%5p --- [%t] %-40.40logger{39} : %m%n")
    private String pattern;

    /**
     * 包名, 自定义当前插件的日志包名, 默认为 BasePlugin 实现类的 包名
     **/
    @XmlTransient
    private String packageName;

    @XmlTransient
    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    @XmlTransient
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    @XmlTransient
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    @XmlTransient
    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
    @XmlTransient
    public String getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(String totalFileSize) {
        this.totalFileSize = totalFileSize;
    }
    @XmlTransient
    public Integer getMaxHistory() {
        return maxHistory;
    }

    public void setMaxHistory(Integer maxHistory) {
        this.maxHistory = maxHistory;
    }
    @XmlTransient
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    @XmlTransient
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
