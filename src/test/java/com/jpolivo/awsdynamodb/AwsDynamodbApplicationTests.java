package com.jpolivo.awsdynamodb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.jpolivo.awsdynamodb.model.Event;
import com.jpolivo.awsdynamodb.service.DynamoDbService;

import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Testcontainers
@SpringBootTest
@Import(AwsTestConfig.class)
class AwsDynamodbApplicationTests {

	@Autowired
	private DynamoDbService dynamoDbService;

	@Container
	static LocalStackContainer localStack = new LocalStackContainer(
			DockerImageName.parse("localstack/localstack:latest")).withServices(DYNAMODB).withEnv("DEFAULT_REGION",
					"us-east-1");

	@BeforeAll
	static void beforeAll() throws UnsupportedOperationException, IOException, InterruptedException {
		localStack.execInContainer("awslocal", "dynamodb", "create-table", "--table-name", "events",
				"--attribute-definitions", "AttributeName=id,AttributeType=S", "--key-schema",
				"AttributeName=id,KeyType=HASH", "--provisioned-throughput",
				"ReadCapacityUnits=5,WriteCapacityUnits=5");
	}

	@AfterAll
	static void afterAll() {
		//
	}

	@Test
	@Order(1)
	void saveTest() throws Exception {
		CompletableFuture<PutItemResponse> event = dynamoDbService.saveEvent(new Event("1", "my event"));
		assertThat(event.get().sdkHttpResponse().isSuccessful(), is(true));
	}

	@Test
	@Order(2)
	void getTest() throws Exception {
		CompletableFuture<Event> event = dynamoDbService.getEvent("1");
		assertThat(event.get(), is(new Event("1", "my event")));
	}

}
