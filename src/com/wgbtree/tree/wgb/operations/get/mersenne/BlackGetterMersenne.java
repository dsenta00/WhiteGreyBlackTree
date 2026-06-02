package com.wgbtree.tree.wgb.operations.get.mersenne;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.handler.EntryHandler;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.operations.get.dec.GreyGetterDec;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackGetterMersenne {

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMin(Black<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(black.getGreys())
				.filter(Objects::nonNull)
				.map(GreyGetterMersenne::getMin)
				.filter(Objects::nonNull)
				.min(Entry.comparingByKey())
				.orElse(black.getEntries().lastEntry());
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMax(Black<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		return black.getEntries().firstEntry();
	}

	public static <T, K extends Comparable<K>>
	void getAllAsc(List<Entry<K, Set<T>>> list, Black<K, T> black) {
		if (isNull(black)) {
			return;
		}

		var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(black.getCapacity());

		for (var grey : black.getGreys()) {
			var res = GreyGetterMersenne.getAllAsc(grey);
			results.add(res.iterator());
		}

		EntryHandler.mergeAsc(list, results);

		// Now add the entries of the current node in descending order
		for (int i = black.getEntries().size() - 1; i >= 0; i--) {
			list.add(black.getEntries().get(i));
		}
	}

	public static <T, K extends Comparable<K>>
	void getAllDesc(List<Entry<K, Set<T>>> list, Black<K, T> black) {
		if (isNull(black)) {
			return;
		}

		list.addAll(black.getEntries());

		var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(black.getCapacity());

		for (var grey : black.getGreys()) {
			var res = GreyGetterMersenne.getAllDesc(grey);
			results.add(res.iterator());
		}

		EntryHandler.mergeDesc(list, results);
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(List<Entry<K, Set<T>>> list, Black<K, T> black, K from, K to) {
		if (isNull(black)) {
			return;
		}

		var entries = black.getEntries();

		if (from.compareTo(entries.firstEntry().getKey()) > 0) {
			return;
		}

		if (from.compareTo(entries.lastEntry().getKey()) < 0) {
			var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(black.getCapacity());

			for (var grey : black.getGreys()) {
				var res = GreyGetterMersenne.getBetweenAsc(grey, from, to);
				results.add(res.iterator());
			}

			EntryHandler.mergeAsc(list, results);
		}

		if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
			int index = entries.searchClosest(to);

			for (int i = black.getEntries().size() - 1; i >= index; i--) {
				list.add(black.getEntries().get(i));
			}
		}
	}
}
