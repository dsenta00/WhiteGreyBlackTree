package com.wgbtree.tree.wgb.operations.get.power;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.handler.EntryHandler;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.operations.get.acc.GreyGetterAcc;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackGetterPower {

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMin(Black<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(black.getGreys())
				.filter(Objects::nonNull)
				.map(GreyGetterPower::getMin)
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
			var res = GreyGetterAcc.getAllAsc(grey);
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
			var res = GreyGetterAcc.getAllDesc(grey);
			results.add(res.iterator());
		}

		EntryHandler.mergeDesc(list, results);
	}
}
