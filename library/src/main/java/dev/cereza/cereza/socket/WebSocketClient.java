package dev.cereza.cereza.socket;

import dev.cereza.cereza.utils.queue.MessengerQueue;
import dev.cereza.cereza.utils.subscriber.SubscriberFn;

public class WebSocketClient extends MessengerQueue<String> {
	private final WebSocketExtern socket;

	public WebSocketClient(String address) {
		this.socket = new WebSocketExtern(address);

		this.pauseProcessing();

		this.socket.subscribe("open", (__) -> {
			this.resumeProcessing();
		});

		this.socket.subscribe("error", (__) -> {
			this.pauseProcessing();
		});
	}

	public void addEventListener(String event, SubscriberFn<Object> fn) {
		this.socket.subscribe(event, fn);
	}

	public void forceSendData(String data) {
		this.socket.send(data);
	}
}
