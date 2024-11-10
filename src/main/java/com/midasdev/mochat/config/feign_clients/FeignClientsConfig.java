package com.midasdev.mochat.config.feign_clients;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.midasdev.mochat")
public class FeignClientsConfig {

}
