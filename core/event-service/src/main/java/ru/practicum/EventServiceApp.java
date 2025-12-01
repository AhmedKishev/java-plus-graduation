package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.client.RequestClient;
import ru.practicum.client.UserClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {UserClient.class, RequestClient.class})
public class EventServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApp.class, args);
    }
}