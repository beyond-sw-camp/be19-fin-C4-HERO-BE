package com.c4.hero.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider; // ★ 이거 추가됨!
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    // 1. 뒤에 ':'를 붙여서, yml에 값이 없으면 빈 문자열("")이 들어오게 방어
    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        // 2. 핵심: 키가 비어있으면 IAM Role (Instance Profile) 사용
        if (accessKey.isEmpty() || secretKey.isEmpty()) {
            return AmazonS3ClientBuilder
                    .standard()
                    .withRegion(region)
                    .withCredentials(InstanceProfileCredentialsProvider.getInstance())
                    .build();
        }

        // 3. 키가 있으면 로컬 개발용 Access Key 사용
        else {
            BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            return AmazonS3ClientBuilder
                    .standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();
        }
    }
}