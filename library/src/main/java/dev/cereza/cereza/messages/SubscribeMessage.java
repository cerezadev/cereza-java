package dev.cereza.cereza.messages;

public record SubscribeMessage(String topic) {
	public SubscribeMessage(String topic) {
		this.topic = topic;
	}
}
