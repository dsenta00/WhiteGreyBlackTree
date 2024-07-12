package com.wgbtree.tree.whitegreyblackplus.operations.delete;

import com.wgbtree.tree.whitegreyblackplus.entries.EntriesList.LeakPolicy;
import com.wgbtree.tree.whitegreyblackplus.node.BNode;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import com.wgbtree.tree.whitegreyblackplus.node.delete.RemoveResult;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GNodeRemover {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(GNode<K, T> node, K key, int keyHash) {
		if (node == null) {
			return RemoveResult.empty();
		}

		var entries = node.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		// Try to remove from the grey node
		var removedEntry = entries.remove(key);

		if (removedEntry != null) {
			if (node.getLeakPolicy() == LeakPolicy.SMALLEST && node.getCountRight() > 0) {
				var minResult = BNodeRemover.removeMin(node.getRight());
				entries.add(minResult.getEntry());
				node.setRight((BNode<K, T>) minResult.getNode());
				node.decCountRight();
			} else if (/*node.getLeakPolicy() == LeakPolicy.LARGEST && */ node.getCountLeft() > 0) {
				var maxResult = WNodeRemover.removeMax(node.getLeft());
				entries.add(maxResult.getEntry());
				node.setLeft((WNode<K, T>) maxResult.getNode());
				node.decCountLeft();
			}
		} else if (entries.lastEntry().getKey().compareTo(key) < 0) {
			var result = BNodeRemover.remove(node.getRight(), key, keyHash);
			node.setRight((BNode<K, T>) result.getNode());
			removedEntry = result.getEntry();
			node.decCountRight();
		} else if (entries.firstEntry().getKey().compareTo(key) > 0) {
			var result = WNodeRemover.remove(node.getLeft(), key, keyHash);
			node.setLeft((WNode<K, T>) result.getNode());
			removedEntry = result.getEntry();
			node.decCountLeft();
		}

		if (entries.isEmpty()) {
			node = null;
		}

		return RemoveResult.of(node, removedEntry);
	}

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> removeMax(GNode<K, T> node) {
		if (node == null) {
			return RemoveResult.empty();
		}

		if (node.getRight() != null) {
			var result = BNodeRemover.removeMax(node.getRight());
			node.setRight((BNode<K, T>) result.getNode());
			node.decCountRight();
			return RemoveResult.of(node, result.getEntry());
		} else {
			var entries = node.getEntries();
			if (entries.isEmpty()) {
				return RemoveResult.empty();
			}

			var maxEntry = entries.lastEntry();
			K key = maxEntry.getKey();
			int keyHash = key.hashCode();
			return remove(node, key, keyHash);
		}
	}

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> removeMin(GNode<K, T> node) {
		if (node == null) {
			return RemoveResult.empty();
		}

		if (node.getLeft() != null) {
			var result = WNodeRemover.removeMin(node.getLeft());
			node.setLeft((WNode<K, T>) result.getNode());
			node.decCountLeft();
			return RemoveResult.of(node, result.getEntry());
		} else {
			var entries = node.getEntries();
			if (entries.isEmpty()) {
				return RemoveResult.empty();
			}

			var minEntry = entries.firstEntry();
			K key = minEntry.getKey();
			int keyHash = key.hashCode();
			return remove(node, key, keyHash);
		}
	}
}
