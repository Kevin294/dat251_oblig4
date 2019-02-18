package inf226;

import java.util.Iterator;

import inf226.Maybe.NothingException;

/**
 * 
 * A simple immutable linked list.
 * 
 * @author INF226
 *
 * @param <T> Object type
 */
public class ImmutableLinkedList<T> implements Iterable<T> {
	private final Maybe<Pair<T, ImmutableLinkedList<T>>> head;
	private final int size;

	public ImmutableLinkedList() {
		head = Maybe.nothing();
		size = 0;
	}

	public ImmutableLinkedList(final T head, final ImmutableLinkedList<T> tail) {
		this.head = Maybe.just(new Pair<T, ImmutableLinkedList<T>>(head, tail));
		this.size = tail.size + 1;
	}

	Maybe<T> getHead() {
		return head.map(Pair.<T, ImmutableLinkedList<T>>firstProjection());
	}

	Maybe<ImmutableLinkedList<T>> getTail() {
		return head.map(Pair.<T, ImmutableLinkedList<T>>secondProjection());
	}

	@Override
	public Iterator<T> iterator() {
		return new ListIterator<T>(this);
	}

	private static final class ListIterator<U> implements Iterator<U> {
		private ImmutableLinkedList<U> list;

		public ListIterator(ImmutableLinkedList<U> list) {
			this.list = list;
		}

		@Override
		public boolean hasNext() {
			return !(list.head.isNothing());
		}

		@Override
		public U next() {
			try {
				final U elem = list.getHead().force();
				list = list.getTail().force();
				return elem;
			} catch (NothingException e) {
				return null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
