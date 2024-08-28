package com.wgbtree.tree.wgb.operations.get.dec;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.black.Black;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackGetterDec {

	public static <K extends Comparable<K>, T> Map.Entry<K, Set<T>> getMin(Black<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(black.getGreys())
				.filter(Objects::nonNull)
				.map(GreyGetterDec::getMin)
				.filter(Objects::nonNull)
				.min(Map.Entry.comparingByKey())
				.orElse(black.getEntries().lastEntry());
	}

	public static <K extends Comparable<K>, T> Map.Entry<K, Set<T>> getMax(Black<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		return black.getEntries().firstEntry();
	}

	public static <T, K extends Comparable<K>> void getAllAsc(List<Set<T>> list, Black<K, T> black) {
		if (isNull(black)) {
			return;
		}

		var heapTree = new MinHeapTree<K, T>();

		for (var grey : black.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllAsc(heapTree, grey);
			}
		}

		list.addAll(heapTree.popAll());

		// Now add the entries of the current node in descending order
		for (int i = black.getEntries().size() - 1; i >= 0; i--) {
			list.add(black.getEntries().get(i).getValue());
		}
	}

	public static <T, K extends Comparable<K>> void getAllAsc(MinHeapTree<K, T> heapTree, Black<K, T> black) {
		if (isNull(black)) {
			return;
		}

		for (var grey : black.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllAsc(heapTree, grey);
			}
		}

		for (var entry : black.getEntries()) {
			heapTree.push(entry);
		}
	}

	public static <T, K extends Comparable<K>> void getAllDesc(List<Set<T>> list, Black<K, T> black) {
		if (isNull(black)) {
			return;
		}

		var heapTree = new MaxHeapTree<K, T>();

		for (var grey : black.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllDesc(heapTree, grey);
			}
		}

		list.addAll(heapTree.popAll());
	}

	public static <K extends Comparable<K>, T> void getAllDesc(MaxHeapTree<K, T> heapTree, Black<K, T> black) {
		if (isNull(black)) {
			return;
		}

		for (var entry : black.getEntries()) {
			heapTree.push(entry);
		}

		for (var grey : black.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllDesc(heapTree, grey);
			}
		}
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(List<Set<T>> list, Black<K, T> black, K from, K to) {
		if (isNull(black)) {
			return;
		}

		var entries = black.getEntries();

		if (from.compareTo(entries.firstEntry().getKey()) > 0) {
			return;
		}

		if (from.compareTo(entries.lastEntry().getKey()) < 0) {
			var minHeapTree = new MinHeapTree<K, T>();
			for (var grey : black.getGreys()) {
				if (grey != null) {
					GreyGetterDec.getBetweenAsc(minHeapTree, grey, from, to);
				}
			}

			list.addAll(minHeapTree.popAll());
		}

		if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
			int index = entries.searchClosest(to);

			for (int i = black.getEntries().size() - 1; i >= index; i--) {
				list.add(black.getEntries().get(i).getValue());
			}
		}
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(MinHeapTree<K, T> minHeapTree, Black<K, T> black, K from, K to) {
		if (isNull(black)) {
			return;
		}

		var entries = black.getEntries();

		if (from.compareTo(entries.firstEntry().getKey()) > 0) {
			return;
		}

		if (from.compareTo(entries.lastEntry().getKey()) < 0) {
			for (var grey : black.getGreys()) {
				if (grey != null) {
					GreyGetterDec.getBetweenAsc(minHeapTree, grey, from, to);
				}
			}
		}

		if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
			int index = entries.searchClosest(to);

			for (int i = entries.size() - 1; i >= index; i--) {
				minHeapTree.push(black.getEntries().get(i));
			}
		}
	}
}
