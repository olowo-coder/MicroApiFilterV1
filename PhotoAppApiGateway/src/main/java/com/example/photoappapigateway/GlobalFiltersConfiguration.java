package com.example.photoappapigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GlobalFiltersConfiguration {

    @Bean
    @Order(1)
    public GlobalFilter secondFilter(){
        return (exchange, chain) -> {
            log.info("My second global pre-filter .....");
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                log.info("My second global post-filter .....");
            }));
        };
    }

    @Bean
    @Order(2)
    public GlobalFilter thirdFilter(){
        return (exchange, chain) -> {
            log.info("My third global pre-filter .....");
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                log.info("My third global post-filter .....");
            }));
        };
    }

}
