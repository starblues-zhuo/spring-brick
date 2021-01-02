package com.mybatis.main.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author starBlues
 * @version 1.0
 * @since 2020-12-20
 */
@Controller
public class ThymeleafController {

    @GetMapping(value = "show")
    public String show(Model model){
        model.addAttribute("uid","123456789");
        model.addAttribute("name","Jerry");
        return "show";
    }

}
