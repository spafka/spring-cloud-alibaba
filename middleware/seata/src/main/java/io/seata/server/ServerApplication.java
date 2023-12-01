package io.seata.server;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author spilledyear@outlook.com
 */
@SpringBootApplication(scanBasePackages = {"io.seata"})
public class ServerApplication {
    public static void main(String[] args) throws IOException {
        // run the spring-boot application
        SpringApplication.run(ServerApplication.class, args);
    }
}