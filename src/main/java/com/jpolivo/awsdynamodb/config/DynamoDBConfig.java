package com.jpolivo.awsdynamodb.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.SystemProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
@Slf4j
public class DynamoDBConfig {

	@Value("${dynamodb.endpoint:http://localhost:8000/}")
	private String dynamoEndpoint;

	@Value("${aws.accesskey}")
	private String accessKey;

	@Value("${aws.secretkey}")
	private String secretKey;

	@Bean
	public AwsBasicCredentials awsBasicCredentials() {
		return AwsBasicCredentials.create(accessKey, secretKey);
	}

	@Bean
	DynamoDbAsyncClient awsDynamoDB(AwsBasicCredentials awsBasicCredentials) {
		
		DynamoDbAsyncClientBuilder clientBuilder = DynamoDbAsyncClient.builder();
		log.info("Configuring DynamoDbAsyncClientBuilder with credentials -> accessKey:{} - secretKey: {}", awsBasicCredentials.accessKeyId(), awsBasicCredentials.secretAccessKey());
		
		clientBuilder.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials));

		if (!dynamoEndpoint.isEmpty()) {
			log.info("Overriding dynamoEndpoint on DynamoDbAsyncClientBuilder with {}", dynamoEndpoint);
			clientBuilder.endpointOverride(URI.create(dynamoEndpoint));
		}
		
		log.info("System Property aws.region: {}", SystemProperties.get("aws.region"));
		log.info("Environment Var AWS_REGION: {}", System.getenv("AWS_REGION"));
		
		return clientBuilder.build();
	}
	
	/*@Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient() {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(getDynamoDbAsyncClient())
                .build();
    }*/
}