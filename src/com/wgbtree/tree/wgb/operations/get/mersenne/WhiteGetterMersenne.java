package com.wgbtree.tree.wgb.operations.get.mersenne;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.handler.EntryHandler;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteGetterMersenne {

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMin(White<K, T> white) {
		if (isNull(white) || white.getEntries().isEmpty()) {
			return null;
		}

		return white.getEntries().firstEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMax(White<K, T> white) {
		if (isNull(white) || white.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(white.getGreys())
				.filter(Objects::nonNull)
				.map(GreyGetterMersenne::getMax)
				.filter(Objects::nonNull)
				.min(Entry.comparingByKey())
				.orElse(white.getEntries().lastEntry());
	}

	public static <T, K extends Comparable<K>>
	void getAllAsc(List<Entry<K, Set<T>>> list, White<K, T> white) {
		if (isNull(white)) {
			return;
		}

        list.addAll(white.getEntries());

		var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

		for (var grey : white.getGreys()) {
			var res = GreyGetterMersenne.getAllAsc(grey);
			results.add(res.iterator());
		}

		EntryHandler.mergeAsc(list, results);
	}

	public static <T, K extends Comparable<K>>
	void getAllDesc(List<Entry<K, Set<T>>> list, White<K, T> white) {
		if (isNull(white)) {
			return;
		}

		var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

		for (var grey : white.getGreys()) {
			var res = GreyGetterMersenne.getAllDesc(grey);
			results.add(res.iterator());
		}

		EntryHandler.mergeDesc(list, results);

		for (int i = white.getEntries().size() - 1; i >= 0; i--) {
			list.add(white.getEntries().get(i));
		}
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(List<Entry<K, Set<T>>> list, White<K, T> white, K from, K to) {
		if (isNull(white)) {
			return;
		}

		var entries = white.getEntries();

		if (to.compareTo(entries.firstEntry().getKey()) < 0) {
			return;
		}

		if (from.compareTo(entries.lastEntry().getKey()) <= 0) {
			int index = entries.searchClosest(from);

			for (int i = index; i < entries.size(); i++) {
				list.add(entries.get(i));
			}
		}

		if (to.compareTo(entries.lastEntry().getKey()) > 0) {
			var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(white.getCapacity());

			for (var grey : white.getGreys()) {
				var res = GreyGetterMersenne.getBetweenAsc(grey, from, to);
				results.add(res.iterator());
			}

			EntryHandler.mergeAsc(list, results);
		}
	}
}
