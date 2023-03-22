package com.example.examservice.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

import static com.amazonaws.services.s3.internal.Constants.MB;


/**
 * @author Le Hoang Nhat a.k.a Rei202
 * @Date 3/21/2023
 */
@Configuration
public class S3Config {
    @Value("${amazon.s3.region}")
    private String region;
    @Value("${amazon.s3.bucket-name}")

    private String bucketName;
    @Value("${amazon.s3.access-key}")

    private String accessKey;
    @Value("${amazon.s3.secret-key}")

    private String secretKey;

    @Bean
    public AmazonS3 createS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_1)
                .build();
        return amazonS3;
    }


    @Bean
    public TransferManager createTransferManager() {
        TransferManager transferManager = TransferManagerBuilder.standard()
                .withS3Client(createS3Client())
                .withDisableParallelDownloads(false)
                .withMinimumUploadPartSize(Long.valueOf (5 * MB))
                .withMultipartUploadThreshold(Long.valueOf(16 * MB))
                .build();
        return transferManager;
    }
//    private ThreadPoolExecutor createExcutorThread
}