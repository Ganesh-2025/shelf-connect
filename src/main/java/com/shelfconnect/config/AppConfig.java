package com.shelfconnect.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class AppConfig {
    private int pageSize = 10;
    private int maxPageSize = 100;
    private int pageNo = 1;
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableHandlerMethodArgumentResolver(){
        return pageableResolver -> {
            pageableResolver.setFallbackPageable(PageRequest.of(pageNo,pageSize));
            pageableResolver.setMaxPageSize(maxPageSize);
            pageableResolver.setOneIndexedParameters(true);
        };
    }
}
