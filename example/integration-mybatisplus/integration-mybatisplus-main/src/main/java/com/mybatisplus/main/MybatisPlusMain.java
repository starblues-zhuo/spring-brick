package com.mybatisplus.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主启动程序
 *
 * @author zhangzhuo
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = {  "com.mybatisplus.main" })
@MapperScan("com.mybatisplus.main.mapper")
public class MybatisPlusMain {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusMain.class, args);
    }

}
