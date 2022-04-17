package dev.cereza.cereza.messages;

public record PublishMessage(String topic, Object data) {
	public PublishMessage(String topic, Object data) {
		this.topic = topic;
		this.data = data;
	}
}
