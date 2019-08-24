package com.persistence.plugin1.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Service
public class TestService {

    @Autowired
    private Gson gson;

    public void gson(){
        System.out.println(gson.toJson("123"));
    }

}
