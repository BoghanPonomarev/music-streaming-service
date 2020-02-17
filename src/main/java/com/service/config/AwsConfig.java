package com.service.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

  @Bean
  AmazonS3 amazonS3() {
    String accessKey = "AKIAJUWW7BIG7HQQN4JQ";
    String secretKey = "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";

    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    AmazonS3ClientBuilder standard = AmazonS3ClientBuilder.standard();
    standard.setCredentials(new AWSStaticCredentialsProvider(credentials));
    return standard.build();
  }

}
