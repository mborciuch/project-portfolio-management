package com.mbor.spring;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.mbor.dao", "com.mbor.service", "com.mbor.mapping", "com.mbor.spring"})
public class ServiceConfiguration {

    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }

}