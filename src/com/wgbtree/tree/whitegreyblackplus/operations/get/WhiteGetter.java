package com.wgbtree.tree.whitegreyblackplus.operations.get;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.whitegreyblackplus.node.White;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteGetter {

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
				.map(GreyGetter::getMax)
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
				GreyGetter.getAllAsc(heapTree, grey);
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
				GreyGetter.getAllAsc(heapTree, grey);
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
				GreyGetter.getAllDesc(heapTree, grey);
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
				GreyGetter.getAllDesc(heapTree, grey);
			}
		}
	}
}
