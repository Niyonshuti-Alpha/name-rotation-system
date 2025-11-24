package com.project.namerotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication

@EntityScan("com.project.namerotationsystem.model")
public class NameRotationSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(NameRotationSystemApplication.class, args);
    }
}
