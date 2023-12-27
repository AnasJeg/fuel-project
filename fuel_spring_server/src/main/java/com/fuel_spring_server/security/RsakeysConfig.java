package com.fuel_spring_server.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsakeysConfig(RSAPublicKey publicKey) {
}