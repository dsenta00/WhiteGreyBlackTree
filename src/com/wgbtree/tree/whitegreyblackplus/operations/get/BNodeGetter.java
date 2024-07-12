package com.wgbtree.tree.whitegreyblackplus.operations.get;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.whitegreyblackplus.node.BNode;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BNodeGetter {

	public static <K extends Comparable<K>, T> Map.Entry<K, Set<T>> getMin(BNode<K, T> node) {
		if (isNull(node) || node.getEntries().isEmpty()) {
			return null;
		}

		return Arrays.stream(node.getGNodes())
				.filter(Objects::nonNull)
				.map(GNodeGetter::getMin)
				.filter(Objects::nonNull)
				.min(Map.Entry.comparingByKey())
				.orElse(node.getEntries().lastEntry());
	}

	public static <K extends Comparable<K>, T> Map.Entry<K, Set<T>> getMax(BNode<K, T> node) {
		if (isNull(node) || node.getEntries().isEmpty()) {
			return null;
		}

		return node.getEntries().firstEntry();
	}

	public static <T, K extends Comparable<K>> void getAllAsc(List<Set<T>> list, BNode<K, T> node) {
		if (isNull(node)) {
			return;
		}

		var heapTree = new MinHeapTree<K, T>();

		for (var gNode : node.getGNodes()) {
			if (gNode != null) {
				GNodeGetter.getAllAsc(heapTree, gNode);
			}
		}

		list.addAll(heapTree.popAll());

		// Now add the entries of the current node in descending order
		for (int i = node.getEntries().size() - 1; i >= 0; i--) {
			list.add(node.getEntries().get(i).getValue());
		}
	}

	public static <T, K extends Comparable<K>> void getAllAsc(MinHeapTree<K, T> heapTree, BNode<K, T> node) {
		if (isNull(node)) {
			return;
		}

		for (var gNode : node.getGNodes()) {
			if (gNode != null) {
				GNodeGetter.getAllAsc(heapTree, gNode);
			}
		}

		for (var entry : node.getEntries()) {
			heapTree.push(entry);
		}
	}

	public static <T, K extends Comparable<K>> void getAllDesc(List<Set<T>> list, BNode<K, T> node) {
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
	}

	public static <K extends Comparable<K>, T> void getAllDesc(MaxHeapTree<K, T> heapTree, BNode<K, T> node) {
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
