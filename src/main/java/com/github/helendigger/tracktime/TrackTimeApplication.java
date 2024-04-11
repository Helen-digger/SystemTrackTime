package com.github.helendigger.tracktime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TrackTimeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackTimeApplication.class, args);
    }
}
