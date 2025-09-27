package com.shelfconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class AppConfig {
    private final int pageSize = 10;
    private final int maxPageSize = 100;
    private final int pageNo = 1;
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableHandlerMethodArgumentResolver(){
        return pageableResolver -> {
            pageableResolver.setFallbackPageable(PageRequest.of(pageNo,pageSize));
            pageableResolver.setMaxPageSize(maxPageSize);
            pageableResolver.setOneIndexedParameters(true);
        };
    }
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Service API")
                        .version("1.2.0")
                        .description("REST API for My Service")
                        .contact(new Contact().name("Team API").email("api@company.com"))
                );
    }
}
