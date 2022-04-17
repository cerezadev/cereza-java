package dev.cereza.cereza;

import dev.cereza.cereza.messages.PublishMessage;
import dev.cereza.cereza.utils.subscriber.SubscriberFn;
import dev.cereza.cereza.utils.subscriber.UnsubscriberFn;

public class Route<T> {
	private final CerezaApp app;
	private final String topic;
	private final Class<T> type;

	public Route(CerezaApp app, String topic, Class<T> type) {
		this.app = app;
		this.topic = topic;
		this.type = type;
	}

	public void publish(T data) {
		app.send("PUBLISH", new PublishMessage(this.topic, data));
	}

	public UnsubscriberFn subscribe(SubscriberFn<T> fn) {
		return app.subscribe(this.topic, fn, type);
	}
}
