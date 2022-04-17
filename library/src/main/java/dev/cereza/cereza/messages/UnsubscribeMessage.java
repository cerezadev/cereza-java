package dev.cereza.cereza.messages;

public record UnsubscribeMessage(String topic) {
	public UnsubscribeMessage(String topic) {
		this.topic = topic;
	}
}
