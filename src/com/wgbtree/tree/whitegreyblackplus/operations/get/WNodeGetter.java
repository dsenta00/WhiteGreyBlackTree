package com.wgbtree.tree.whitegreyblackplus.operations.get;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WNodeGetter {

	public static <K extends Comparable<K>, T> Map.Entry<K, Set<T>> getMin(WNode<K, T> node) {
		if (isNull(node) || node.getEntries().isEmpty()) {
			return null;
		}

		return node.getEntries().firstEntry();
	}

	public static <K extends Comparable<K>, T> Map.Entry<K, Set<T>> getMax(WNode<K, T> node) {
		if (isNull(node) || node.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(node.getGNodes())
				.filter(Objects::nonNull)
				.map(GNodeGetter::getMax)
				.filter(Objects::nonNull)
				.min(Map.Entry.comparingByKey())
				.orElse(node.getEntries().lastEntry());
	}

	public static <T, K extends Comparable<K>> void getAllAsc(List<Set<T>> list, WNode<K, T> node) {
		if (isNull(node)) {
			return;
		}

		for (var entry : node.getEntries()) {
			list.add(entry.getValue());
		}

		var heapTree = new MinHeapTree<K, T>();
		for (var gNode : node.getGNodes()) {
			if (gNode != null) {
				GNodeGetter.getAllAsc(heapTree, gNode);
			}
		}

		list.addAll(heapTree.popAll());
	}

	public static <T, K extends Comparable<K>> void getAllAsc(MinHeapTree<K, T> heapTree, WNode<K, T> node) {
		if (isNull(node)) {
			return;
		}

		for (var entry : node.getEntries()) {
			heapTree.push(entry);
		}

		for (var gNode : node.getGNodes()) {
			if (gNode != null) {
				GNodeGetter.getAllAsc(heapTree, gNode);
			}
		}
	}

	public static <T, K extends Comparable<K>> void getAllDesc(List<Set<T>> list, WNode<K, T> node) {
		if (isNull(node)) {
			return;
		}

		var heapTree = new MaxHeapTree<K, T>();
		for (var gNode : node.getGNodes()) {
			if (gNode != null) {
				GNodeGetter.getAllDesc(heapTree, gNode);
			}
		}

		list.addAll(heapTree.popAll());

		for (int i = node.getEntries().size() - 1; i >= 0; i--) {
			list.add(node.getEntries().get(i).getValue());
		}
	}

	public static <T, K extends Comparable<K>> void getAllDesc(MaxHeapTree<K, T> heapTree, WNode<K, T> node) {
		if (isNull(node)) {
			return;
		}

		for (var entry : node.getEntries()) {
			heapTree.push(entry);
		}

		for (var gNode : node.getGNodes()) {
			if (gNode != null) {
				GNodeGetter.getAllDesc(heapTree, gNode);
			}
		}
	}
}
