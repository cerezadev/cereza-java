package dev.cereza.cereza;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppOptions {
	private final String projectId;
	private final String server;
	private final ObjectMapper mapper;

	public static AppOptionsBuilder builder() {
		return new AppOptionsBuilder();
	}

	public AppOptions(AppOptionsBuilder builder) {
		this.projectId = builder.getProjectId();
		this.server = builder.getServer();
		this.mapper = builder.getMapper();
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
}
