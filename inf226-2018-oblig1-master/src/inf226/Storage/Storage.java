package inf226.Storage;

import java.sql.SQLException;

/**
 * A general purpose storage interface.
 * Useful for asynchronous storage, as it will only allow
 * updates of newer versions.
 * @author ichor
 *
 * @param <C>
 */
public interface Storage<C > {


	public Stored<C> save(C value) throws SQLException;
	public Stored<C> refresh(Stored<C> old) throws ObjectDeletedException, SQLException;
	public Stored<C> update(Stored<C> old, C newValue) throws ObjectModifiedException,ObjectDeletedException,SQLException;
	public void delete(Stored<C> old) throws ObjectModifiedException,ObjectDeletedException,SQLException;
	
	public static class ObjectModifiedException extends Exception {
		private static final long serialVersionUID = -3013939840856547237L;
		public final Object newObject;

		public ObjectModifiedException(Object newObject) {
			this.newObject = newObject;
		}

	}
	
	public static class ObjectDeletedException extends Exception {
		private static final long serialVersionUID = 4897313573397184472L;
		public final Id id;

		public ObjectDeletedException(Id id) {
			this.id = id;
		}

	}
}
