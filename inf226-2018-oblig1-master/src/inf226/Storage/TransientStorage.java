package inf226.Storage;

import inf226.Maybe;
import inf226.Maybe.NothingException;

import java.util.TreeMap;
import java.util.function.Function;

/*
 * An implementation of KeyedStorage which stores the values
 * in memory.
 * @author INF226
 *
 * @param <K> Key type.
 * @param <C> Value type.
 */

public class TransientStorage<K,C> implements KeyedStorage<K,C> {
	private final Id.Generator id_generator;
	private final TreeMap<Id,Stored<C> > memory;
	private final TreeMap<K,Id> keytable;
	private final Function<C,K> computeKey;
	
	public TransientStorage(final Function<C,K> computeKey) {
		id_generator = new Id.Generator();
		memory = new TreeMap<Id,Stored<C>>();
		keytable = new TreeMap<K,Id>();
		this.computeKey = computeKey;
	}

	public synchronized Stored<C> save(C value) {
		final Stored<C> stored = new Stored<C>(id_generator, value);
		
		memory.put(stored.id(),stored);
		keytable.put(computeKey.apply(value), stored.id());
		return stored;
	}

	public synchronized Stored<C> update(Stored<C> old, C newValue)
			throws ObjectDeletedException, Storage.ObjectModifiedException {
		Stored<C> stored = memory.get(old.id());
		if(stored == null) {
			throw new Storage.ObjectDeletedException(old.id());
		}
		
		if (!stored.equals(old)) {
			throw new Storage.ObjectModifiedException(stored);
		}
		Stored<C> newStored = new Stored<C>(old,newValue);
		memory.put(old.id(), newStored);
		return newStored;
	}

	public synchronized void delete(Stored<C> old) throws ObjectDeletedException, ObjectModifiedException {
		Stored<C> stored = memory.get(old.id());
		if(stored == null) {
			throw new Storage.ObjectDeletedException(old.id());
		}
		
		if (!stored.equals(old)) {
			throw new Storage.ObjectModifiedException(stored);
		}
		
		memory.remove(old.id());
	}

	public Maybe<Stored<C>> lookup(Id key) {
		return new Maybe<Stored<C> >(memory.get(key));
	}

	@Override
	public Maybe<Stored<C>> lookup(K key) {
		Maybe<Id> id = new Maybe<Id>(keytable.get(key));
		if (id.isNothing())
			System.err.println("Key not in store" + key.toString());
		try {
			return lookup(id.force());
		} catch (NothingException e) {
			return Maybe.nothing();
		}
	}

	@Override
	public Stored<C> refresh(Stored<C> old) throws ObjectDeletedException{
		Stored<C> newValue = memory.get(old.id());
		if(newValue == null)
			throw new ObjectDeletedException(old.id());
		return newValue;
	}

}
