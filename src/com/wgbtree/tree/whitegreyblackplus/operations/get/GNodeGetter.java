package com.wgbtree.tree.whitegreyblackplus.operations.get;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.whitegreyblackplus.comparator.EntryComparator;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.node.Node;
import com.wgbtree.tree.whitegreyblackplus.node.delete.GreySearchResult;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GNodeGetter {

	public static <K extends Comparable<K>, T>
	GreySearchResult<K, T> getMin(GNode<K, T> []nodes) {
		if (isNull(nodes) || nodes.length == 0) {
			// arrays is null or empty, return empty result
			return GreySearchResult.empty();
		}

		return IntStream.range(0, nodes.length)
				.mapToObj(i -> GreySearchResult.of(i, getMin(nodes[i])))
				.filter(GreySearchResult::isPresent)
				.min((e1, e2) -> EntryComparator.compare(e1.getEntry(), e2.getEntry()))
				.orElse(GreySearchResult.empty());
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMin(GNode<K, T> node) {
		if (isNull(node)) {
			// Node is empty, return null
			return null;
		}

		if (nonNull(node.getLeft())) {
			return WNodeGetter.getMin(node.getLeft());
		}

		var entries = node.getEntries();

		return entries.isEmpty() ? null : entries.firstEntry();
	}

	public static <K extends Comparable<K>, T>
	GreySearchResult<K, T> getMax(GNode<K, T> []nodes) {
		if (isNull(nodes) || nodes.length == 0) {
			// arrays is null or empty, return empty result
			return GreySearchResult.empty();
		}

		return IntStream.range(0, nodes.length)
				.mapToObj(i -> GreySearchResult.of(i, getMax(nodes[i])))
				.filter(GreySearchResult::isPresent)
				.max((e1, e2) -> EntryComparator.compare(e1.getEntry(), e2.getEntry()))
				.orElse(GreySearchResult.empty());
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMax(GNode<K, T> node) {
		if (isNull(node)) {
			// Node is empty, return null
			return null;
		}

		if (nonNull(node.getRight())) {
			return BNodeGetter.getMax(node.getRight());
		}

		var entries = node.getEntries();

		return entries.isEmpty() ? null : entries.lastEntry();
	}

	public static <K extends Comparable<K>, T>
	Set<T> get(GNode<K, T> node, K key, int keyHash) {
		if (isNull(node) || node.getEntries().isEmpty()) {
			// Node is empty, return empty set
			return Set.of();
		}

		for (Node<K, T> n = node; nonNull(n); n = n.nextNode(key, keyHash)) {
			var optionalEntry = n.getEntries().find(key);

			if (optionalEntry.isPresent()) {
				return optionalEntry.get().getValue();
			}
		}

		return Set.of();
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getAllAsc(GNode<K, T> gNode) {
		List<Set<T>> list = new LinkedList<>();
		getAllAsc(list, gNode);
		return list;
	}

	public static <T, K extends Comparable<K>> void getAllAsc(List<Set<T>> list, GNode<K, T> gNode) {
		if (isNull(gNode)) {
			return;
		}

		WNodeGetter.getAllAsc(list, gNode.getLeft());
		gNode.getEntries().forEach(entry -> list.add(entry.getValue()));
		BNodeGetter.getAllAsc(list, gNode.getRight());
	}

	public static <T, K extends Comparable<K>> void getAllAsc(MinHeapTree<K, T> heapTree, GNode<K, T> gNode) {
		if (isNull(gNode)) {
			return;
		}

		WNodeGetter.getAllAsc(heapTree, gNode.getLeft());
		gNode.getEntries().forEach(heapTree::push);
		BNodeGetter.getAllAsc(heapTree, gNode.getRight());
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getAllDesc(GNode<K, T> gNode) {
		List<Set<T>> list = new LinkedList<>();
		getAllDesc(list, gNode);
		return list;
	}

	public static <T, K extends Comparable<K>> void getAllDesc(List<Set<T>> list, GNode<K, T> gNode) {
		if (isNull(gNode)) {
			return;
		}

		BNodeGetter.getAllDesc(list, gNode.getRight());
		gNode.getEntries().forEach(entry -> list.add(entry.getValue()));
		WNodeGetter.getAllDesc(list, gNode.getLeft());
	}

	public static <K extends Comparable<K>, T> void getAllDesc(MaxHeapTree<K, T> heapTree, GNode<K, T> gNode) {
		if (isNull(gNode)) {
			return;
		}

		BNodeGetter.getAllDesc(heapTree, gNode.getRight());
		gNode.getEntries().forEach(heapTree::push);
		WNodeGetter.getAllDesc(heapTree, gNode.getLeft());
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getInAsc(GNode<K, T> gNode, List<K> keys) {
		if (gNode == null || keys == null || keys.isEmpty()) {
			return List.of();
		}

		if (keys.size() == 1) {
			K key = keys.remove(0);
			int keyHash = key == null ? 0 : key.hashCode();
			var result = get(gNode, key, keyHash);

			return result.isEmpty() ? List.of() : List.of(result);
		}

		while (!keys.isEmpty()) {
			K firstKey = keys.remove(0);
			var minEntry = gNode.getEntries().firstEntry();

			if (minEntry.getKey() == null) {
				if (firstKey == null) {
					// TODO Finish this
				}
			}

		}

		throw new UnsupportedOperationException("Not implemented yet");
	}
}
