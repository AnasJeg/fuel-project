package com.fuel_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class FuelGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuelGatewayApplication.class, args);
    }

    @Bean
    public DiscoveryClientRouteDefinitionLocator routage(ReactiveDiscoveryClient rpc, DiscoveryLocatorProperties dlp){
        return new DiscoveryClientRouteDefinitionLocator(rpc,dlp);
    }
}
