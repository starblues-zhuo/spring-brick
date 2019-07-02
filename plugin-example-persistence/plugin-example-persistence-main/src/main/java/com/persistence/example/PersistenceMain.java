package com.persistence.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@SpringBootApplication(
        scanBasePackages = { "com.persistence.example" })
@MapperScan("com.persistence.example.mapper")
public class PersistenceMain {

    public static void main(String[] args) {
        SpringApplication.run(PersistenceMain.class, args);
    }

}
