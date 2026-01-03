package com.example.auth_service.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Client;
import feign.okhttp.OkHttpClient;
@Configuration
public class FeignConfig {
	//for supporting patch in feign
    @Bean
    public Client feignClient() {
        return new OkHttpClient();
    }
}