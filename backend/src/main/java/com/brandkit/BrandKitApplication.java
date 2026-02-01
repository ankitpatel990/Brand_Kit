package com.brandkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BrandKit Backend Application
 * 
 * B2B-focused digital platform for customizable promotional merchandise.
 * This application provides authentication, catalog, customization, and order management APIs.
 * 
 * @see FRD-001 User Registration and Authentication
 */
@SpringBootApplication
@EnableScheduling
public class BrandKitApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrandKitApplication.class, args);
    }
}
