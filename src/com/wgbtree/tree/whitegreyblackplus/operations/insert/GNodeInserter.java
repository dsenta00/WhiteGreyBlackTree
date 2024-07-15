package com.wgbtree.tree.whitegreyblackplus.operations.insert;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;
import com.wgbtree.tree.whitegreyblackplus.creator.GNodeCreator;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.operations.rotator.GNodeRotator;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GNodeInserter {

	public static <K extends Comparable<K>, T> GNode<K, T> insert(GNode<K, T> node,
																  K key,
																  Set<T> value,
																  int keyHash,
																  int order,
																  int rank,
																  AtomicReference<T> oldValue,
																  boolean allowDuplicates) {
		if (isNull(node)) {
			return GNodeCreator.create(order, key, value, allowDuplicates);
		}

		var entries = node.getEntries();

		if (!entries.isFull()) {
			insertHere(node, key, value);
			return node;
		}

		if (entries.lastEntry().getKey().compareTo(key) < 0) {
			var result = insertRight(node, key, value, keyHash, order, rank, oldValue, allowDuplicates);
			GNodeRotator.tryRotateLeft(node, order, rank, allowDuplicates);
			return result;
		}

		if (entries.firstEntry().getKey().compareTo(key) > 0) {
			var result = insertLeft(node, key, value, keyHash, order, rank, oldValue, allowDuplicates);
			GNodeRotator.tryRotateRight(node, order, rank, allowDuplicates);
			return result;
		}

		var leakPolicy = node.setLeakPolicy();
		var leakedEntry = insertHere(node, key, value);

		if (leakedEntry.getKey() != key) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = isNull(key) ? 0 : key.hashCode();
		}

		if (leakPolicy == LeakPolicy.SMALLEST) {
			return insertLeft(node, key, value, keyHash, order, rank, oldValue, allowDuplicates);
		} else {
			return insertRight(node, key, value, keyHash, order, rank, oldValue, allowDuplicates);
		}
	}

	/**
	 * Inserts the entry in the current node
	 *
	 * @param node the node to insert the entry in
	 * @param key the key of the entry
	 * @param value the value of the entry
	 * @return the entry that was leaked
	 */
	private static <K extends Comparable<K>, T> Entry<K, Set<T>> insertHere(GNode<K, T> node,
																			K key,
																			Set<T> value) {
		var entry = new SimpleEntry<>(key, value);
		var leakedEntryStorage = new AtomicReference<Entry<K, Set<T>>>();

		node.getEntries().add(entry, leakedEntryStorage);

		return leakedEntryStorage.get();
	}

	private static <K extends Comparable<K>, T> GNode<K, T> insertLeft(GNode<K, T> node,
																	   K key,
																	   Set<T> value,
																	   int keyHash,
																	   int order,
																	   int rank,
																	   AtomicReference<T> oldValue,
																	   boolean allowDuplicates) {
		var leftNode = WNodeInserter.insert(node.getLeft(), key, value, keyHash, order, rank, oldValue, allowDuplicates);

		node.setLeft(leftNode);
		if (oldValue != null) {
			node.incCountLeft();
		}

		return node;
	}

	private static <K extends Comparable<K>, T> GNode<K, T> insertRight(GNode<K, T> node,
																		K key,
																		Set<T> value,
																		int keyHash,
																		int order,
																		int rank,
																		AtomicReference<T> oldValue,
																		boolean allowDuplicates) {
		var rightNode = BNodeInserter.insert(node.getRight(), key, value, keyHash, order, rank, oldValue, allowDuplicates);

		node.setRight(rightNode);
		if (oldValue != null) {
			node.incCountRight();
		}

		return node;
	}
}
