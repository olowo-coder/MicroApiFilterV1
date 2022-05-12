package com.example.photoappdiscovery.photoappdiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class PhotoAppDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoAppDiscoveryApplication.class, args);
    }

}
