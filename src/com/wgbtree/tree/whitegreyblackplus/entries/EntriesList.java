package com.wgbtree.tree.whitegreyblackplus.entries;

import lombok.Getter;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.whitegreyblackplus.constants.Constants.LINEAR_SEARCH_THRESHOLD;

public abstract class EntriesList<K extends Comparable<K>, T> implements List<Entry<K, Set<T>>> {

	public enum Order {
		ASCENDING, DESCENDING
	}

	public enum LeakPolicy {
		SMALLEST, LARGEST
	}

	private final Entry<K, Set<T>>[] array;
	private int size;
	@Getter
	private final int capacityLimit;

	public EntriesList(int capacityLimit) {
		this.capacityLimit = capacityLimit;
		this.array = (Entry<K, Set<T>>[]) new Entry<?, ?>[capacityLimit];
		this.size = 0;
	}

	protected EntriesList(EntriesList<K, T> entriesList) {
		this.capacityLimit = entriesList.capacityLimit;
		this.array = Arrays.copyOf(entriesList.array, entriesList.array.length);
		this.size = entriesList.size;
	}

	public Entry<K, Set<T>> firstEntry() {
		return array[0];
	}

	public Entry<K, Set<T>> lastEntry() {
		return array[size - 1];
	}

	protected abstract boolean allowMergingOnSameKey();
	public abstract Order order();
	public abstract LeakPolicy leakPolicy();
	public abstract EntriesList<K, T> setPolicy(LeakPolicy leakPolicy);

	public boolean add(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		if (entry == null) {
			throw new NullPointerException();
		}

		Optional<Entry<K, Set<T>>> existingEntryOptional = find(entry.getKey());
		if (existingEntryOptional.isPresent()) {
			var entryToChange = existingEntryOptional.get();

			if (!allowMergingOnSameKey()) {
				var oldValue = entryToChange.getValue();
				entryToChange.setValue(entry.getValue());
				leakedEntry.set(new SimpleEntry<>(entryToChange.getKey(), oldValue));
				return true;
			}

			existingEntryOptional.get().getValue().addAll(entry.getValue());
		} else {
			if (size >= capacityLimit) {
				if (leakPolicy() == LeakPolicy.SMALLEST) {
					if (order() == Order.ASCENDING) {
						handleLeakOfFirstEntryAsc(entry, leakedEntry);
					} else {
						handleLeakOfLastEntryDesc(entry, leakedEntry);
					}
				} else {
					if (order() == Order.ASCENDING) {
						handleLeakOfLastEntryAsc(entry, leakedEntry);
					} else {
						handleLeakOfFirstEntryDesc(entry, leakedEntry);
					}
				}
			} else {
				array[size++] = entry;
			}

			sortList();
		}
		return true;
	}

	public boolean add(Entry<K, Set<T>> entry) {
		return add(entry, new AtomicReference<>());
	}

	@Override
	@Deprecated(since = "Should use remove(K key, AtomicReference<Entry<K, Set<T>>> leakedEntry) instead.")
	@SuppressWarnings("unchecked")
	public boolean remove(Object key) {
		return remove((K) key, new AtomicReference<>());
	}

	public Entry<K, Set<T>> remove(K key) {
		var leakedEntry = new AtomicReference<Entry<K, Set<T>>>();
		remove(key, leakedEntry);
		return leakedEntry.get();
	}

	private boolean remove(K key, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		// Reset leaked entry
		leakedEntry.set(null);

		int index = findByIndex(key);

		if (index >= 0) {
			var entry = array[index];
			System.arraycopy(array, index + 1, array, index, size - index - 1);
			array[--size] = null;
			leakedEntry.set(entry);
			return true;
		}

		return false;
	}

	public Optional<Entry<K, Set<T>>> find(K key) {
		int index = findByIndex(key);
		if (index >= 0) {
			return Optional.of(array[index]);
		} else {
			return Optional.empty();
		}
	}

	protected int findByIndex(K key) {
		if (size == 0) {
			return -1;
		}

		int index = -1;
		if (key == null) {
			for (int i = 0; i < size; i++) {
				if (array[i].getKey() == null) {
					index = i;
					break;
				}
			}
		} else if (size() < LINEAR_SEARCH_THRESHOLD) {
			for (int i = 0; i < size; i++) {
				if (array[i].getKey().equals(key)) {
					index = i;
					break;
				}
			}
		} else {
			index = binarySearch(key);
		}

		return index;
	}

	protected int binarySearch(K key) {
		int low = 0;
		int high = size - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			K midKey = array[mid].getKey();
			int cmp = midKey.compareTo(key);

			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid; // Key found
			}
		}
		return -1;  // Key not found
	}

	protected void handleLeakOfFirstEntryAsc(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		if (entry.getKey().compareTo(array[0].getKey()) > 0) {
			leakedEntry.set(array[0]);
			array[0] = entry;
		} else {
			leakedEntry.set(entry);
		}
	}

	protected void handleLeakOfFirstEntryDesc(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		if (entry.getKey().compareTo(array[0].getKey()) > 0) {
			leakedEntry.set(entry);
		} else {
			leakedEntry.set(array[0]);
			array[0] = entry;
		}
	}

	protected void handleLeakOfLastEntryAsc(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		if (entry.getKey().compareTo(array[size - 1].getKey()) < 0) {
			leakedEntry.set(array[size - 1]);
			array[size - 1] = entry;
		} else {
			leakedEntry.set(entry);
		}
	}

	protected void handleLeakOfLastEntryDesc(Entry<K, Set<T>> entry, AtomicReference<Entry<K, Set<T>>> leakedEntry) {
		if (entry.getKey().compareTo(array[size - 1].getKey()) < 0) {
			leakedEntry.set(entry);
		} else {
			leakedEntry.set(array[size - 1]);
			array[size - 1] = entry;
		}
	}

	private void sortList() {
		if (order() == Order.ASCENDING) {
			Arrays.sort(array, 0, size, Entry.comparingByKey());
		} else {
			Arrays.sort(array, 0, size, Entry.comparingByKey(Comparator.reverseOrder()));
		}
	}

	@Override
	public int size() {
		return size;
	}

	public boolean isFull() {
		return size == capacityLimit;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		return find((K) o).isPresent();
	}

	@Override
	public Iterator<Entry<K, Set<T>>> iterator() {
		return new Iterator<>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public Entry<K, Set<T>> next() {
				return array[i++];
			}
		};
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}


	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Map.Entry<K, Set<T>>> c) {
		for (Map.Entry<K, Set<T>> entry : c) {
			add(entry);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends Map.Entry<K, Set<T>>> c) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			modified |= remove(o);
		}

		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		for (int i = 0; i < size; i++) {
			if (!c.contains(array[i])) {
				remove(array[i]);
			}
		}

		return true;
	}

	@Override
	public void clear() {
		Arrays.fill(array, null);
		size = 0;
	}

	@Override
	public Map.Entry<K, Set<T>> get(int index) {
		return array[index];
	}

	@Override
	public Map.Entry<K, Set<T>> set(int index, Map.Entry<K, Set<T>> element) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void add(int index, Map.Entry<K, Set<T>> element) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Map.Entry<K, Set<T>> remove(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}

		if (index == size - 1) {
			return array[--size];
		}

		Map.Entry<K, Set<T>> entry = array[index];
		System.arraycopy(array, index + 1, array, index, size - index - 1);
		size--;

		return entry;
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public ListIterator<Map.Entry<K, Set<T>>> listIterator() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public ListIterator<Map.Entry<K, Set<T>>> listIterator(int index) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public List<Map.Entry<K, Set<T>>> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
}

