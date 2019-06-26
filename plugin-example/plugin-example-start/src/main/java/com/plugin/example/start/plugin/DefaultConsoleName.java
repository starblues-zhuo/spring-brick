package com.plugin.example.start.plugin;

import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Component
public class DefaultConsoleName implements ConsoleName{
    @Override
    public String name() {
        return "My name is Main-start-DefaultConsoleName";
    }
}
