package inf226.Storage;

/**
 * A stored value. Acts as a capability of updating or
 * deleting a value from a Storage.
 * @author INF226
 *
 * @param <T> Value type.
 */
public final class Stored<T> {
	public final Version version;
	private final T value;

	public Stored(Stored<T> old, T newValue){
		this.value = newValue;
		this.version = old.version.increment();
	}
	
	public Stored(Id.Generator generator, T value) {
		this.value = value;
		this.version = new Version(generator.fresh());
	}

	// Copy constructor
	public Stored(Version version, T newValue){
		this.value = newValue;
		this.version = version;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || getClass() != obj.getClass())
		    return false;
		@SuppressWarnings("unchecked")
		Stored<T> stored = (Stored<T>)obj;
		return this.version.equals((stored).version);
	}

	public T getValue() {
		return value;
	}

	public Id id() {
		return version.id;
	}
}
