package com.wgbtree.tree.wgb.operations.get.dec;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteGetterDec {

	public static <K extends Comparable<K>, T>
	Map.Entry<K, Set<T>> getMin(White<K, T> white) {
		if (isNull(white) || white.getEntries().isEmpty()) {
			return null;
		}

		return white.getEntries().firstEntry();
	}

	public static <K extends Comparable<K>, T>
	Map.Entry<K, Set<T>> getMax(White<K, T> white) {
		if (isNull(white) || white.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(white.getGreys())
				.filter(Objects::nonNull)
				.map(GreyGetterDec::getMax)
				.filter(Objects::nonNull)
				.min(Map.Entry.comparingByKey())
				.orElse(white.getEntries().lastEntry());
	}

	public static <T, K extends Comparable<K>>
	void getAllAsc(List<Set<T>> list, White<K, T> white) {
		if (isNull(white)) {
			return;
		}

		for (var entry : white.getEntries()) {
			list.add(entry.getValue());
		}

		var heapTree = new MinHeapTree<K, T>();
		for (var grey : white.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllAsc(heapTree, grey);
			}
		}

		list.addAll(heapTree.popAll());
	}

	public static <T, K extends Comparable<K>>
	void getAllAsc(MinHeapTree<K, T> heapTree, White<K, T> white) {
		if (isNull(white)) {
			return;
		}

		for (var entry : white.getEntries()) {
			heapTree.push(entry);
		}

		for (var grey : white.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllAsc(heapTree, grey);
			}
		}
	}

	public static <T, K extends Comparable<K>>
	void getAllDesc(List<Set<T>> list, White<K, T> white) {
		if (isNull(white)) {
			return;
		}

		var heapTree = new MaxHeapTree<K, T>();
		for (var grey : white.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllDesc(heapTree, grey);
			}
		}

		list.addAll(heapTree.popAll());

		for (int i = white.getEntries().size() - 1; i >= 0; i--) {
			list.add(white.getEntries().get(i).getValue());
		}
	}

	public static <T, K extends Comparable<K>>
	void getAllDesc(MaxHeapTree<K, T> heapTree, White<K, T> white) {
		if (isNull(white)) {
			return;
		}

		for (var entry : white.getEntries()) {
			heapTree.push(entry);
		}

		for (var grey : white.getGreys()) {
			if (grey != null) {
				GreyGetterDec.getAllDesc(heapTree, grey);
			}
		}
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(List<Set<T>> list, White<K, T> white, K from, K to) {
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
				list.add(entries.get(i).getValue());
			}
		}

		if (to.compareTo(entries.lastEntry().getKey()) > 0) {
			var minHeapTree = new MinHeapTree<K, T>();
			for (var grey : white.getGreys()) {
				if (grey != null) {
					GreyGetterDec.getBetweenAsc(minHeapTree, grey, from, to);
				}
			}

			list.addAll(minHeapTree.popAll());
		}
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(MinHeapTree<K, T> minHeapTree, White<K, T> white, K from, K to) {
		if (isNull(white)) {
			return;
		}

		var entries = white.getEntries();

		if (to.compareTo(entries.firstEntry().getKey()) < 0) {
			return;
		}

		if (from.compareTo(entries.lastEntry().getKey()) <= 0) {
			int index = entries.searchClosest(to);

			for (int i = index; i < entries.size(); i++) {
				minHeapTree.push(entries.get(i));
			}
		}

		if (to.compareTo(entries.lastEntry().getKey()) > 0) {
			for (var grey : white.getGreys()) {
				if (grey != null) {
					GreyGetterDec.getBetweenAsc(minHeapTree, grey, from, to);
				}
			}
		}
	}
}
