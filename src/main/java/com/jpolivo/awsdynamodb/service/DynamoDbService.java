package com.jpolivo.awsdynamodb.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jpolivo.awsdynamodb.model.Event;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Service
@Slf4j
public class DynamoDbService {
	private static final String TABLE_NAME = "events";
	private static final String ID_COLUMN = "id";
	private static final String BODY_COLUMN = "body";

	private final DynamoDbAsyncClient client;

	@Autowired
	public DynamoDbService(DynamoDbAsyncClient client) {
		this.client = client;
	}

	public CompletableFuture<PutItemResponse> saveEvent(Event event) {
		log.info("Saving event...");

		Map<String, AttributeValue> item = new HashMap<>();

		item.put(ID_COLUMN, AttributeValue.builder().s(event.getId()).build());
		item.put(BODY_COLUMN, AttributeValue.builder().s(event.getBody()).build());

		PutItemRequest putItemRequest = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();

		return client.putItem(putItemRequest);
	}
	

	public CompletableFuture<Event> getEvent(String id) {
		log.info("Getting event...");

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(ID_COLUMN, AttributeValue.builder().s(id).build());

		GetItemRequest getRequest = GetItemRequest.builder().tableName(TABLE_NAME).key(key).attributesToGet(BODY_COLUMN)
				.build();

		return client.getItem(getRequest).thenApply(item -> {
			if (!item.hasItem()) {
				log.warn("There is not event with key {}", id);
				return null;
			} else {
				Map<String, AttributeValue> itemAttr = item.item();
				String body = itemAttr.get(BODY_COLUMN).s();
				return new Event(id, body);
			}
		});
	}

}