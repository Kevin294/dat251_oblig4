package inf226;

import java.util.function.Function;

public class Pair<T1, T2> {
	public final T1 first;
	public final T2 second;

	public Pair(final T1 first, final T2 second) {
		this.first = first;
		this.second = second;
	}

	public static <U, V> Function<Pair<U, V>, U> firstProjection() {
		return new Function<Pair<U, V>, U>() {
			@Override
			public U apply(Pair<U, V> arg0) {
				return arg0.first;
			}
		};
	}

	public static <U, V> Function<Pair<U, V>, V> secondProjection() {
		return new Function<Pair<U, V>, V>() {
			@Override
			public V apply(Pair<U, V> arg0) {
				return arg0.second;
			}
		};
	}
}
