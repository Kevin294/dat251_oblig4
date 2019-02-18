package inf226;

import java.util.function.Function;

/**
 * A simple implementation of an option type to avoid null-checks.
 * 
 * @author INF226
 *
 * @param <T>
 */
public class Maybe<T> {
	private final T value;

	/**
	 * Empty constructor
	 */
	private Maybe() {
		super();
		value = null;
	}

	/**
	 * Value constructor
	 * 
	 * @param value
	 */
	public Maybe(T value) {
		super();
		this.value = value;
	}

	/**
	 * @return The empty value
	 */
	public static <U> Maybe<U> nothing() {
		return new Maybe<U>();
	}

	/**
	 * Wrap a value into Maybe.
	 * 
	 * @param value The underlying value.
	 * @return Wrapped value.
	 */
	public static <U> Maybe<U> just(U value) {
		return new Maybe<U>(value);
	}

	/**
	 * Check if there is an underlying value
	 * 
	 * @return true if there is a value, false otherwise
	 */
	public boolean isNothing() {
		return (value == null);
	}

	/**
	 * Compose a Maybe-value with a Maybe-retuning function.
	 * 
	 * @param f The binding-function
	 * @return Resulting Maybe-value
	 */
	public <U> Maybe<U> bind(Function<T, Maybe<U>> f) {
		if (this.isNothing()) {
			return nothing();
		}
		return f.apply(value);
	}

	/**
	 * Map a function on the underlying value if any.
	 * 
	 * @param f The function to apply to the value.
	 * @return The new Maybe with the mapped value.
	 */
	public <U> Maybe<U> map(Function<T, U> f) {
		if (this.isNothing()) {
			return nothing();
		}
		return just(f.apply(value));
	}

	/**
	 * Force the Maybe object to return a value.
	 * 
	 * @return The underlying value.
	 * @throws NothingException If there is no value.
	 */
	public T force() throws NothingException {
		if (this.isNothing())
			throw new NothingException();
		return value;
	}

	public static class NothingException extends Exception {
		private static final long serialVersionUID = 8141663034597379968L;

		public NothingException() {
			super("Unexpected nothing.");
		}

		public NothingException(String msg) {
			super(msg);
		}

	}
}
