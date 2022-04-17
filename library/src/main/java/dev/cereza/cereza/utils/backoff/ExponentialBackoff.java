package dev.cereza.cereza.utils.backoff;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ExponentialBackoff {
	public static Supplier<Double> expBackoff() {
		return ExponentialBackoff.expBackoff(64000);
	}

	public static Supplier<Double> expBackoff(int maxBackoff) {
		final AtomicInteger n = new AtomicInteger(0);

		return () -> {
			final double timeSecs = Math.pow(2, n.getAndAdd(1)) + Math.random();
			final double timeMillis = Math.ceil(timeSecs * 1000);

			return Math.min(timeMillis, maxBackoff);
		};
	}
}
