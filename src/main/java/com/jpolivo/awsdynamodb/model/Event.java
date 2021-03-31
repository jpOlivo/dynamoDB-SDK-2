package com.jpolivo.awsdynamodb.model;

import java.util.Objects;

public class Event {
	private String id;
	private String body;

	public Event() {
	}

	public Event(String id, String body) {
		this.id = id;
		this.body = body;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Event event = (Event) o;
		return id.equals(event.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
