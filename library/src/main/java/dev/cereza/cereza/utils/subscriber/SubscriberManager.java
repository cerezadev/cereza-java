package dev.cereza.cereza.utils.subscriber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubscriberManager {
	private final Map<String, Set<SubscriberFn<Object>>> subscribers = new HashMap<>();

	public UnsubscriberFn subscribe(String id, SubscriberFn<Object> fn) {
		final Set<SubscriberFn<Object>> idSubscribers = this.subscribers.computeIfAbsent(id, (__) -> new HashSet<>());

		idSubscribers.add(fn);

		return () -> this.unsubscribe(id, fn);
	}

	public void unsubscribe(String id, SubscriberFn<Object> fn) {
		final Set<SubscriberFn<Object>> idSubscribers = this.getSubscribers(id);

		idSubscribers.remove(fn);

		if (idSubscribers.size() == 0) {
			this.subscribers.remove(id);
		}
	}

	public Set<String> getSubscribersIds() {
		return this.subscribers.keySet();
	}

	public boolean hasSubscriberId(String id) {
		return this.subscribers.containsKey(id);
	}

	public Set<SubscriberFn<Object>> getSubscribers(String id) {
		return this.subscribers.getOrDefault(id, new HashSet<>());
	}

	public void invokeSubscribers(String id, Object data) {
		for (SubscriberFn<Object> subscriber : this.getSubscribers(id)) {
			subscriber.invoke(data);
		}
	}
}
