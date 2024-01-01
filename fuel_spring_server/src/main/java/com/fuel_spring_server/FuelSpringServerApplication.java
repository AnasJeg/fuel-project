package com.fuel_spring_server;

import com.fuel_spring_server.security.RsakeysConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(RsakeysConfig.class)
public class FuelSpringServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuelSpringServerApplication.class, args);
    }

}
