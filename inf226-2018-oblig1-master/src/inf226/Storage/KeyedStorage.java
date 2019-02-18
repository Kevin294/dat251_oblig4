package inf226.Storage;

import inf226.Maybe;

/**
 * This interface is for storages where one can lookup values by keys.
 * 
 * @author INF226
 *
 * @param <K> Key type
 * @param <C> Value type
 */
public interface KeyedStorage<K,C> extends Storage<C> {
	Maybe<Stored<C>> lookup(K key);
}
