package dev.cereza.cereza.utils.queue;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public abstract class MessengerQueue<T> {
	private final Queue<T> queue = new LinkedList<>();
	private boolean paused = false;

	public void sendData(T data) {
		this.queue.add(data);
		this.processQueue();
	}

	protected void pauseProcessing() {
		this.paused = true;
	}

	protected void resumeProcessing() {
		this.paused = false;

		this.processQueue();
	}

	private void processQueue() {
		if (this.paused)
			return;

		Optional<T> element = this.dequeue();

		while (element.isPresent()) {
			this.forceSendData(element.get());

			element = this.dequeue();
		}
	}

	private Optional<T> dequeue() {
		return Optional.ofNullable(this.queue.poll());
	}

	public abstract void forceSendData(T data);
}
