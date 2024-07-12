package com.wgbtree.tree.whitegreyblackplus.operations.delete;

import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import com.wgbtree.tree.whitegreyblackplus.node.delete.RemoveResult;
import com.wgbtree.tree.whitegreyblackplus.operations.get.GNodeGetter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WNodeRemover {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(WNode<K, T> node, K key, int keyHash) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		// Try to remove from the white node
		var removedEntry = entries.remove(key);

		if (removedEntry != null) {
			suckMinFromGreyNodes(node);
		} else {
			var lastEntry = entries.lastEntry();
			if (lastEntry != null && lastEntry.getKey().compareTo(key) < 0) {
				// Entry is not in the white node
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

	public static <T, K extends Comparable<K>> RemoveResult<K, T> removeMax(WNode<K, T> node) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var maxResult = GNodeGetter.getMax(node.getGNodes());
		Map.Entry<K, Set<T>> removedEntry = null;
		if (maxResult.isEmpty()) {
			// No grey nodes, remove maximum from the white node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			var gNodes = node.getGNodes();
			int i = maxResult.getIndex();
			K key = maxResult.getEntry().getKey();
			int keyHash = key.hashCode();
			var result = GNodeRemover.remove(gNodes[i], key, keyHash);
			gNodes[i] = (GNode<K, T>) result.getNode();

			if (result.getEntry() == null) {
				throw new IllegalStateException("Entry not found in the grey node");
			}
			removedEntry = result.getEntry();
		}

		if (entries.isEmpty()) {
			node = null;
		}

		return RemoveResult.of(node, removedEntry);
	}

	public static <T, K extends Comparable<K>> RemoveResult<K, T> removeMin(WNode<K, T> node) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var removedEntry = entries.remove(entries.firstEntry().getKey());
		suckMinFromGreyNodes(node);

		if (entries.isEmpty()) {
			node = null;
		}

		return RemoveResult.of(node, removedEntry);
	}

	private static <K extends Comparable<K>, T>
	void suckMinFromGreyNodes(WNode<K, T> node) {
		var gNodes = node.getGNodes();

		var minResult = GNodeGetter.getMin(gNodes);
		if (minResult.isEmpty()) {
			return;
		}

		int i = minResult.getIndex();
		K key = minResult.getEntry().getKey();
		int keyHash = key.hashCode();
		var result = GNodeRemover.remove(gNodes[i], key, keyHash);
		gNodes[i] = (GNode<K, T>) result.getNode();
	}
}
