package com.panto.attendance.security;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:auth.properties")
@Data
public class SecurityConfigurationProvider {
    @Value("SECRET")
    private String secret;
    @Value("${EXPIRATION_DATE}")
    private long expirationTime;

    public static String Secret;
    public static long ExpirationTime;
    public static String TokenPrefix = "Bearer ";
    public static String HeaderString = "Authorization";
    //private String signUpUrl = "/api/account/";

    @Value("${SECRET}")
    public void setSecretStatic(String secret){
        SecurityConfigurationProvider.Secret = secret;
    }

    @Value("${EXPIRATION_DATE}")
    public void setExpirationTimeStatic(long expirationTime){
        SecurityConfigurationProvider.ExpirationTime = expirationTime;
    }
}
