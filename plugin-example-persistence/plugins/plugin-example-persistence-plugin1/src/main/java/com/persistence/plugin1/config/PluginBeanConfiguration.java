package com.persistence.plugin1.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Configuration
public class PluginBeanConfiguration {

    @Bean
    public Gson gson(){
        return new Gson();
    }


}
