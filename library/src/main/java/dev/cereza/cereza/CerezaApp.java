package dev.cereza.cereza;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cereza.cereza.messages.Message;
import dev.cereza.cereza.messages.PublishMessage;
import dev.cereza.cereza.messages.RegisterClientMessage;
import dev.cereza.cereza.messages.SubscribeMessage;
import dev.cereza.cereza.messages.UnsubscribeMessage;
import dev.cereza.cereza.socket.WebSocketClient;
import dev.cereza.cereza.utils.subscriber.SubscriberFn;
import dev.cereza.cereza.utils.subscriber.SubscriberManager;
import dev.cereza.cereza.utils.subscriber.UnsubscriberFn;

public class CerezaApp {
	private final AppOptions options;
	private final WebSocketClient socket;
	private final SubscriberManager topicSubscribers = new SubscriberManager();

	public CerezaApp(AppOptions options) {
		this.options = options;
		this.socket = new WebSocketClient(options.getServer());

		this.setupListeners();
	}

	public <T> Route<T> route(String topic, Class<T> type) {
		return new Route<T>(this, topic, type);
	}

	protected <T> UnsubscriberFn subscribe(String topic, SubscriberFn<T> fn, Class<T> type) {
		final SubscriberFn<Object> mapperFn = (objData) -> {
			final ObjectMapper mapper = options.getMapper();
			final T tData = mapper.convertValue(objData, type);

			fn.invoke(tData);
		};

		this.topicSubscribers.subscribe(topic, mapperFn);
		this.register(topic);

		return () -> this.unsubscribe(topic, mapperFn);
	}

	protected void send(String type, Object data) {
		this.send(type, data, false);
	}

	protected void send(String type, Object data, boolean force) {
		try {
			final ObjectMapper mapper = this.options.getMapper();
			final Message message = new Message(type, data);
			final String json = mapper.writeValueAsString(message);

			if (force)
				this.socket.forceSendData(json);
			else
				this.socket.sendData(json);
		} catch (final JsonProcessingException ex) {
			ex.printStackTrace();
		}
	}

	private void unsubscribe(String topic, SubscriberFn<Object> fn) {
		this.topicSubscribers.unsubscribe(topic, fn);

		if (!this.topicSubscribers.hasSubscriberId(topic)) {
			this.unregister(topic);
		}
	}

	private void register(String topic) {
		this.send("SUBSCRIBE", new SubscribeMessage(topic));
	}

	private void unregister(String topic) {
		this.send("UNSUBSCRIBE", new UnsubscribeMessage(topic));
	}

	private void setupListeners() {
		this.registerMessageListener();

		this.socket.addEventListener("beforeopen", (__) -> {
			this.registerClient();
		});

		this.socket.addEventListener("open", (__) -> {
			this.registerSubscribers();
		});
	}

	private void registerMessageListener() {
		this.socket.addEventListener("message", (messageData) -> {
			try {
				final ObjectMapper mapper = this.options.getMapper();
				final Message message = mapper.readValue(messageData.toString(), Message.class);
				final String messageType = message.type();

				if (!messageType.equals("PUBLISH"))
					return;

				final PublishMessage publishMessage = mapper.convertValue(message.data(), PublishMessage.class);
				final String topic = publishMessage.topic();
				final Object data = publishMessage.data();

				this.topicSubscribers.invokeSubscribers(topic, data);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}

	private void registerClient() {
		this.send("REGISTER_CLIENT", new RegisterClientMessage(this.options.getProjectId()), true);
	}

	private void registerSubscribers() {
		for (final String topic : this.topicSubscribers.getSubscribersIds()) {
			this.register(topic);
		}
	}
}
