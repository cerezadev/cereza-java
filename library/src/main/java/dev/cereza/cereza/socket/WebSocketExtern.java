package dev.cereza.cereza.socket;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import dev.cereza.cereza.utils.backoff.ExponentialBackoff;
import dev.cereza.cereza.utils.subscriber.SubscriberFn;
import dev.cereza.cereza.utils.subscriber.SubscriberManager;
import dev.cereza.cereza.utils.subscriber.UnsubscriberFn;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketExtern extends WebSocketClient {
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private final SubscriberManager subscribers = new SubscriberManager();
	private Supplier<Double> expWaitTime;

	public WebSocketExtern(String address) {
		super(URI.create(address));

		this.connect();
	}

	public void connect() {
		this.connect(0);
	}

	public void connect(double delay) {
		executorService.schedule(() -> {
			super.connect();

			this.expWaitTime = ExponentialBackoff.expBackoff();
		}, (long) delay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void send(String text) {
		try {
			super.send(text);
		} catch (Exception ex) {
		}
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		this.subscribers.invokeSubscribers("beforeopen", null);
		this.subscribers.invokeSubscribers("open", null);
	}

	@Override
	public void onMessage(String message) {
		this.subscribers.invokeSubscribers("message", message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		this.subscribers.invokeSubscribers("close", null);
	}

	@Override
	public void onError(Exception ex) {
		this.connect(this.expWaitTime.get());

		this.subscribers.invokeSubscribers("error", ex);
	}

	public UnsubscriberFn subscribe(String id, SubscriberFn<Object> fn) {
		return this.subscribers.subscribe(id, fn);
	}
}
