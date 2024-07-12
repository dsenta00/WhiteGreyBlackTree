package com.wgbtree.tree.whitegreyblackplus.operations.delete;

import com.wgbtree.tree.whitegreyblackplus.node.BNode;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.node.delete.RemoveResult;
import com.wgbtree.tree.whitegreyblackplus.operations.get.GNodeGetter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BNodeRemover {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(BNode<K, T> node, K key, int keyHash) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		// Try to remove from the black node
		var removedEntry = entries.remove(key);

		if (removedEntry != null) {
			suckMaxFromGreyNodes(node);
		} else {
			var lastEntry = entries.lastEntry();
			if (lastEntry != null && (key == null || lastEntry.getKey().compareTo(key) > 0)) {
				// Entry is not in the black node
				// Try to remove from the grey nodes
				var gNodes = node.getGNodes();
				int i = Math.abs(keyHash) % node.getCapacity();
				var result = GNodeRemover.remove(gNodes[i], key, keyHash);
				gNodes[i] = (GNode<K, T>) result.getNode();

				removedEntry = result.getEntry();
			}
		}

		if (entries.isEmpty()) {
			node = null;
		}

		return RemoveResult.of(node, removedEntry);
	}

	public static <T, K extends Comparable<K>>
	RemoveResult<K, T> removeMax(BNode<K, T> node) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var removedEntry = entries.remove(entries.firstEntry().getKey());
		suckMaxFromGreyNodes(node);

		if (entries.isEmpty()) {
			node = null;
		}

		return RemoveResult.of(node, removedEntry);
	}

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> removeMin(BNode<K, T> node) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var minResult = GNodeGetter.getMin(node.getGNodes());
		Map.Entry<K, Set<T>> removedEntry = null;
		if (minResult.isEmpty()) {
			// No grey nodes, remove minimum from the black node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			var gNodes = node.getGNodes();
			int i = minResult.getIndex();
			K key = minResult.getEntry().getKey();
			int keyHash = key.hashCode();
			var result = GNodeRemover.remove(gNodes[i], key, keyHash);
			gNodes[i] = (GNode<K, T>) result.getNode();
			removedEntry = result.getEntry();
		}

		if (entries.isEmpty()) {
			node = null;
		}

		return RemoveResult.of(node, removedEntry);
	}

	private static <K extends Comparable<K>, T>
	void suckMaxFromGreyNodes(BNode<K, T> node) {
		var gNodes = node.getGNodes();

		var maxResult = GNodeGetter.getMax(gNodes);
		if (maxResult.isEmpty()) {
			return;
		}

		int i = maxResult.getIndex();
		K key = maxResult.getEntry().getKey();
		int keyHash = key.hashCode();
		var result = GNodeRemover.remove(gNodes[i], key, keyHash);
		gNodes[i] = (GNode<K, T>) result.getNode();
	}
}
