package com.jpolivo.awsdynamodb.config;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.jpolivo.awsdynamodb.model.Event;
import com.jpolivo.awsdynamodb.service.DynamoDbService;

import reactor.core.publisher.Mono;

@Configuration
public class HttpRouterConfig {

	@Bean
	public RouterFunction<ServerResponse> eventRouter(DynamoDbService dynamoDbService) {
		EventHandler eventHandler = new EventHandler(dynamoDbService);
		return route(GET("/eventfn/{id}").and(accept(APPLICATION_JSON)), eventHandler::handleGetEvent).andRoute(
				POST("/eventfn").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
				eventHandler::handleSaveEvent);
	}

	static class EventHandler {
		private final DynamoDbService dynamoDbService;

		public EventHandler(DynamoDbService dynamoDbService) {
			this.dynamoDbService = dynamoDbService;
		}

		Mono<ServerResponse> handleGetEvent(ServerRequest request) {
			String eventId = request.pathVariable("id");
			Mono<Event> eventMono = Mono.fromFuture(dynamoDbService.getEvent(eventId));
			return ServerResponse.ok().body(eventMono, Event.class);
		}

		Mono<ServerResponse> handleSaveEvent(ServerRequest request) {
			Mono<Event> eventMono = request.bodyToMono(Event.class);
	        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
	                .body(Mono.from(eventMono.map(dynamoDbService::saveEvent)), Event.class);
			
		}
	}
}
