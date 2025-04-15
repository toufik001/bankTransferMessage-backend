package com.bnq.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bnq.api.mapper.MessageMapper;
import com.bnq.api.mapper.PartnerMapper;

@Configuration
public class MapperConfig {
    
    @Bean
    public MessageMapper messageMapper() {
        return new MessageMapper();
    }
    
    @Bean
    public PartnerMapper partnerMapper() {
        return new PartnerMapper();
    }
}