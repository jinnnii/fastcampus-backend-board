package com.fastcampus.backendboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class FastcampusBackendBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastcampusBackendBoardApplication.class, args);
    }

}
