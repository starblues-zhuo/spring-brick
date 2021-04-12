package com.log.plugin.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping("/log/plugin")
public class PluginRest {

    private static final Logger log = LoggerFactory.getLogger(PluginRest.class);

    @GetMapping("/print")
    public String print(@RequestParam("value") String value){
        log.info("Request value: {}", value);
        return value;
    }

}
