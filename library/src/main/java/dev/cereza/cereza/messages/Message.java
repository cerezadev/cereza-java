package dev.cereza.cereza.messages;

public record Message(String type, Object data) {
	public Message(String type, Object data) {
		this.type = type;
		this.data = data;
	}
}
