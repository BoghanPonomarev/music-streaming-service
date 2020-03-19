package com.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.service.dao")
@SpringBootApplication(scanBasePackages = "com.service")
public class MusicStreamingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MusicStreamingServiceApplication.class, args);
  }

}
