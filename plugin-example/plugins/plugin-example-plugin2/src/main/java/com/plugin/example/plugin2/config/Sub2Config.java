package com.plugin.example.plugin2.config;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 16:03
 * @Update Date Time:
 * @see
 */
public class Sub2Config {

    private String subName;

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    @Override
    public String toString() {
        return "Sub2Config{" +
                "subName='" + subName + '\'' +
                '}';
    }
}
