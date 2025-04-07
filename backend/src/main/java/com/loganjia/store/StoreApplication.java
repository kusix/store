package com.loganjia.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * main class
 */
@SpringBootApplication
public class StoreApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(StoreApplication.class, args);
    }

}
