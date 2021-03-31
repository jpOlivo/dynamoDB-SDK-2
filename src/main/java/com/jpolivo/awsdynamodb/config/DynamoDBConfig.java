package com.jpolivo.awsdynamodb.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

@Configuration
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
		
		clientBuilder.credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials));

		if (!dynamoEndpoint.isEmpty()) {
			clientBuilder.endpointOverride(URI.create(dynamoEndpoint));
		}
		
		return clientBuilder.build();
	}
	
	/*@Bean
    public DynamoDbEnhancedAsyncClient getDynamoDbEnhancedAsyncClient() {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(getDynamoDbAsyncClient())
                .build();
    }*/
}