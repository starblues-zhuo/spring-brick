package com.log.main.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 插件jar 包测试功能
 * @author sousouki
 * @version 1.0
 */
@RestController
@RequestMapping("/log/main")
public class MainRest {

    private static final Logger log = LoggerFactory.getLogger(MainRest.class);

    @GetMapping("/print")
    public String print(@RequestParam("value") String value){
        log.info("Request value: {}", value);
        return value;
    }

}
