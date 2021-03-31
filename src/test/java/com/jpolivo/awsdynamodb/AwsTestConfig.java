package com.jpolivo.awsdynamodb;

import static com.jpolivo.awsdynamodb.AwsDynamodbApplicationTests.localStack;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@TestConfiguration
public class AwsTestConfig {

	@Bean
	@Primary
	DynamoDbAsyncClient dynamoDbAsyncClient(AwsBasicCredentials awsBasicCredentials) {
		return DynamoDbAsyncClient.builder().endpointOverride(localStack.getEndpointOverride(DYNAMODB))
				.credentialsProvider(StaticCredentialsProvider
						.create(AwsBasicCredentials.create(localStack.getAccessKey(), localStack.getSecretKey())))
				.region(Region.of(localStack.getRegion())).build();

	}
}