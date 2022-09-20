package com.bigcommerce.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class MainConfig {

    @Bean
    public RestTemplate modelMapper(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }
}
