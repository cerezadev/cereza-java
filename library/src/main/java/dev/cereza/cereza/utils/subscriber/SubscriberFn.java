package dev.cereza.cereza.utils.subscriber;

@FunctionalInterface
public interface SubscriberFn<T> {
	public void invoke(T data);
}
