package com.mybatis.plugin1.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-20
 */
@Controller()
@RequestMapping("/thy")
public class Plugin1ThymeleafController {

    @GetMapping()
    public String show(Model model){
        model.addAttribute("uid", UUID.randomUUID().toString());
        model.addAttribute("name","uuid");
        return "test";
    }

}
