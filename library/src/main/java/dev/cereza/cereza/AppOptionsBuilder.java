package dev.cereza.cereza;

import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppOptionsBuilder {
	private String projectId;
	private String server;
	private ObjectMapper mapper = new ObjectMapper();

	public AppOptionsBuilder projectId(String projectId) {
		this.projectId = projectId;
		return this;
	}

	public AppOptionsBuilder server(String server) {
		this.server = server;
		return this;
	}

	public AppOptionsBuilder mapper(ObjectMapper mapper) {
		this.mapper = mapper;
		return this;
	}

	public String getProjectId() {
		return projectId;
	}

	public String getServer() {
		return server;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public AppOptions build() {
		Objects.requireNonNull(projectId, "Project ID is not specified within bulder.");
		Objects.requireNonNull(server, "Server address is not specified within bulder.");
		Objects.requireNonNull(mapper, "Mapper is not specified within bulder.");

		return new AppOptions(this);
	}
}
