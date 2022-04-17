package dev.cereza.cereza.messages;

public record RegisterClientMessage(String projectId) {
	public RegisterClientMessage(String projectId) {
		this.projectId = projectId;
	}
}
