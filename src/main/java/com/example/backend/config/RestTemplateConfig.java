package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //factory.setConnectTimeout(5000); // 5 sec to connect
        //factory.setReadTimeout(5000);    // 5 sec to timeout
        return new RestTemplate();
    }
}
