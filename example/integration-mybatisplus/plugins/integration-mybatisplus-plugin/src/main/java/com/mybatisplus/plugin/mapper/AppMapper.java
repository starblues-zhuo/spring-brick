package com.mybatisplus.plugin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mybatisplus.plugin.entity.App;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @创建人 litao
 * @创建时间 2020/9/8
 */
@Mapper
public interface AppMapper extends BaseMapper<App> {

    List<App> getAppVersion(Integer versionCode);

    boolean addApp(App app);

    App getAppVersionLimit();
}
