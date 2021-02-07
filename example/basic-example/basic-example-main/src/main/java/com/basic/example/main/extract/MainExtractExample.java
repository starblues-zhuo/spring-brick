package com.basic.example.main.extract;

import com.gitee.starblues.annotation.Extract;
import org.springframework.stereotype.Component;

/**
 * @author starBlues
 * @version 1.0
 */
@Extract(bus = "main", scene = "1", useCase = "2")
@Component
public class MainExtractExample implements ExtractExample{


    @Override
    public void exe() {
        System.out.println("Main exe");
    }

    @Override
    public void exe(String name) {
        System.out.println("Main exe, name=" + name);
    }

    @Override
    public void exe(Info info) {
        System.out.println("Main exe, info=" + info);
    }

    @Override
    public Info exeInfo(Info info) {
        info.setName(info.getName() + "-main");
        return info;
    }
}
