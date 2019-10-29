package com.basic.example.plugin1.service;

import com.basic.example.main.plugin.Hello;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class HelloImp1 implements Hello {
    @Override
    public String getName() {
        return "my name is hello imp1 of plugin1";
    }
}
