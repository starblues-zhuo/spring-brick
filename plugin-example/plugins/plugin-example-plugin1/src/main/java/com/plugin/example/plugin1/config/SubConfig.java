package com.plugin.example.plugin1.config;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:03
 * @Update Date Time:
 * @see
 */
public class SubConfig {

    private String subName;

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    @Override
    public String toString() {
        return "SubConfig{" +
                "subName='" + subName + '\'' +
                '}';
    }
}
