package org.cg.stockportfoliomonitoringapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration //  Spring configuration class
public class AppConfig {

    @Bean 
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}