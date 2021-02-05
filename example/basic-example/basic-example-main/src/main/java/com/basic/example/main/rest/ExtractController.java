package com.basic.example.main.rest;

import com.basic.example.main.extract.ExtractExample;
import com.gitee.starblues.factory.process.pipe.extract.ExtractCoordinate;
import com.gitee.starblues.factory.process.pipe.extract.ExtractFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping("/extract")
public class ExtractController {

    @Resource
    private ExtractFactory extractFactory;


    @GetMapping("{name}/exe")
    public void exe(@PathVariable("name") String name){
        ExtractExample extractExample = extractFactory.getExtract(ExtractCoordinate.build(name));
        extractExample.exe();
    }

    @GetMapping("{name}/exeName")
    public void exeName(@PathVariable("name") String name){
        ExtractExample extractExample = extractFactory.getExtract(ExtractCoordinate.build(name));
        extractExample.exe("name");
    }


    @GetMapping("{name}/exeInfo")
    public void exeInfo(@PathVariable("name") String name){
        ExtractExample extractExample = extractFactory.getExtract(ExtractCoordinate.build(name));
        ExtractExample.Info info = new ExtractExample.Info();
        info.setName("plugin2");
        info.setAge(3);
        extractExample.exe(info);
    }


    @GetMapping("{name}/exeR")
    public ExtractExample.Info exeInfoR(@PathVariable("name") String name){
        ExtractExample extractExample = extractFactory.getExtract(ExtractCoordinate.build(name));
        ExtractExample.Info info = new ExtractExample.Info();
        return extractExample.exeInfo(info);
    }



}
