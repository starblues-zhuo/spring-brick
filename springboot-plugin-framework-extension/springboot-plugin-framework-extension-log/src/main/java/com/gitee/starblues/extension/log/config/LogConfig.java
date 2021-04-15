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

    @XmlElement(name = "fileName")
    private String fileName;

    @ConfigItem(defaultValue = "INFO")
    @XmlElement(name = "level")
    private String level;

    @ConfigItem(defaultValue = "10MB")
    @XmlElement(name = "maxFileSize")
    private String maxFileSize;

    @ConfigItem(defaultValue = "10GB")
    @XmlElement(name = "totalFileSize")
    private String totalFileSize;

    @ConfigItem(defaultValue = "30")
    @XmlElement(name = "maxHistory")
    private Integer maxHistory;

    @ConfigItem(defaultValue = "%d{yyyy-MM-dd HH:mm:ss.SSS} -%5p --- [%t] %-40.40logger{39} : %m%n")
    @XmlElement(name = "pattern")
    private String pattern;

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
