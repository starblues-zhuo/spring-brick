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

    /**
     * 日志文件名称
     **/
    @XmlElement(name = "fileName")
    private String fileName;

    /**
     * 日志级别
     **/
    @ConfigItem(defaultValue = "INFO")
    @XmlElement(name = "level")
    private String level;

    /**
     * 日志文件最大容量
     **/
    @ConfigItem(defaultValue = "10MB")
    @XmlElement(name = "maxFileSize")
    private String maxFileSize;

    /**
     * 日志文件总容量
     **/
    @ConfigItem(defaultValue = "10GB")
    @XmlElement(name = "totalFileSize")
    private String totalFileSize;

    /**
     * 最大保存时间
     **/
    @ConfigItem(defaultValue = "30")
    @XmlElement(name = "maxHistory")
    private Integer maxHistory;

    /**
     * 日志内容格式
     **/
    @ConfigItem(defaultValue = "%d{yyyy-MM-dd HH:mm:ss.SSS} -%5p --- [%t] %-40.40logger{39} : %m%n")
    @XmlElement(name = "pattern")
    private String pattern;

    /**
     * 包名
     **/
    @XmlTransient
    private String packageName;

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
