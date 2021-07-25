package com.gitee.starblues.extension.resources.thymeleaf;

import junit.framework.TestCase;
import org.junit.Test;

public class ThymeleafConfigTest extends TestCase {

    @Test
    public void testThymeleafConfig(){
        ThymeleafConfig thymeleafConfig = new ThymeleafConfig();
        thymeleafConfig.setCache(true);
        thymeleafConfig.setEncoding("utf-8");
        thymeleafConfig.setMode("html");
        thymeleafConfig.setSuffix(".html");
        System.out.println(thymeleafConfig);
    }

}