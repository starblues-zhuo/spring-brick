package com.basic.example.main.rest;

import com.basic.example.main.extract.ExtractExample;
import com.gitee.starblues.factory.process.pipe.extract.ExtractCoordinate;
import com.gitee.starblues.factory.process.pipe.extract.ExtractFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping("/extract")
public class ExtractController {

    @Resource
    private ExtractFactory extractFactory;

    @GetMapping("getExtractCoordinates")
    public Map<String, Set<ExtractCoordinate>> getExtractCoordinates(){
        return extractFactory.getExtractCoordinates();
    }

    @GetMapping("getExtractByInterClass")
    public List<String> getExtractByInterClass(){
        List<ExtractExample> extractByInterClass = extractFactory.getExtractByInterClass(ExtractExample.class);
        return extractByInterClass.stream()
                .map(extractExample -> extractExample.getClass().getName())
                .collect(Collectors.toList());
    }


    @GetMapping("{name}/exeR")
    public ExtractExample.Info exeInfoR(@PathVariable("name") String name){
        ExtractExample extractExample = extractFactory.getExtractByCoordinate(ExtractCoordinate.build(name));
        ExtractExample.Info info = new ExtractExample.Info();
        return extractExample.exeInfo(info);
    }



}
