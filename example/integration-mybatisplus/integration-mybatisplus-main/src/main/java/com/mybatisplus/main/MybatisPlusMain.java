package com.mybatisplus.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主启动程序
 *
 * @author starBlues
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {  "com.mybatisplus.main" })
public class MybatisPlusMain {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusMain.class, args);
    }

}
