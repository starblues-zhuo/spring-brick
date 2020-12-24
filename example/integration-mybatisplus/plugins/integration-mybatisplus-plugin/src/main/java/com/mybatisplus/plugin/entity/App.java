package com.mybatisplus.plugin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

/**
 * app版本操作表
 * @创建人 litao
 * @创建时间 2020/9/8
 */
@Alias("app")
public class App implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * APP id
     */
    @TableId(value = "app_id", type = IdType.UUID)
    @ApiModelProperty("app_id")
    private String appId;

    /**
     * app版本名称
     */
    @ApiModelProperty(value = "app版本名称" , required = true)
    private String versionName;

    /**
     * app版本号
     */
    @ApiModelProperty(value = "app版本号" , required = true)
    private Integer versionCode;

    /**
     * 版本说明
     */
    @ApiModelProperty("版本说明")
    private String versionExplanation;

    /**
     * 更新时间戳
     */
    @ApiModelProperty("更新时间--后台插入")
    private String gmtUpdatedTime;



    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionExplanation() {
        return versionExplanation;
    }

    public void setVersionExplanation(String versionExplanation) {
        this.versionExplanation = versionExplanation;
    }

    public String getGmtUpdatedTime() {
        return gmtUpdatedTime;
    }

    public void setGmtUpdatedTime(String gmtUpdatedTime) {
        this.gmtUpdatedTime = gmtUpdatedTime;
    }
}
