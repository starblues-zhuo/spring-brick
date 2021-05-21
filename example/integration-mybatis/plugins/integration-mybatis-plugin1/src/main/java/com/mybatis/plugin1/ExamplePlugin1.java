package com.mybatis.plugin1;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;


/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@ConfigDefinition(fileName = "plugin1-spring.yml", devSuffix = "-dev")
public class ExamplePlugin1 extends BasePlugin  {


    public ExamplePlugin1(PluginWrapper wrapper) {
        super(wrapper);
    }


}
