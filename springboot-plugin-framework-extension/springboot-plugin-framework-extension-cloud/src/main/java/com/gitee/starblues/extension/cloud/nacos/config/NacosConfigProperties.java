package com.gitee.starblues.extension.cloud.nacos.config;

import java.util.Objects;

/**
 * @author starBlues
 * @version 2.4.5
 */
public class NacosConfigProperties {

    private String dataId;

    private String group = "DEFAULT_GROUP";

    private String fileExtension = "properties";

    private boolean refresh = false;

    private boolean followProfile = false;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isFollowProfile() {
        return followProfile;
    }

    public void setFollowProfile(boolean followProfile) {
        this.followProfile = followProfile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NacosConfigProperties)) {
            return false;
        }
        NacosConfigProperties that = (NacosConfigProperties) o;
        return isRefresh() == that.isRefresh() &&
                isFollowProfile() == that.isFollowProfile() &&
                getDataId().equals(that.getDataId()) &&
                getGroup().equals(that.getGroup()) &&
                getFileExtension().equals(that.getFileExtension());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataId(), getGroup(), getFileExtension(), isRefresh(), isFollowProfile());
    }
}
