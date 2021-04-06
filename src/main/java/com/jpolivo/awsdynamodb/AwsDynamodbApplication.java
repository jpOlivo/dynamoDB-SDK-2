package com.jpolivo.awsdynamodb;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.TypeHint;

import com.jpolivo.awsdynamodb.model.Event;

@TypeHint(types = { Event.class, CompletableFuture.class })
@SpringBootApplication
public class AwsDynamodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsDynamodbApplication.class, args);
	}

}
