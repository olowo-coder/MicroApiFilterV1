package com.example.photoappapigateway;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@Slf4j
public class MyPreFilter implements GlobalFilter, Ordered {
    @Autowired
    Environment env;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("My first pre-filter is been executed...");
        String requestPath = exchange.getRequest().getPath().toString();
        log.info("Request path -> " + requestPath);

        HttpHeaders httpHeaders = exchange.getRequest().getHeaders();
        Set<String> headerNames = httpHeaders.keySet();
        headerNames.forEach((header) -> {
            String headerValue = httpHeaders.getFirst(header);
            log.info(header + " " + headerValue);
        });

        ServerHttpRequest request = exchange.getRequest();

        if(new ArrayList<>(
                Arrays.asList("/users-ws/users", "/users-ws/users/login")).contains(request.getPath().toString())){
//            Objects.requireNonNull(exchange.getRequest().getMethod()).toString().equals("POST"))
            return chain.filter(exchange);
        }

        if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
        }

        String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
//            String jwt = authorizationHeader.substring(7);
        String jwt = authorizationHeader.replace("Bearer ", "");
        if(!isJwtValid(jwt)){
            return onError(exchange, "Jwt is not valid", HttpStatus.UNAUTHORIZED);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               String error,
                               HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private boolean isJwtValid(String jwt){
        String subject;
        try {
            subject =  Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody().getSubject();
        }
        catch (Exception ex){
            return false;
        }
        return subject != null && !subject.isEmpty();
    }
}
