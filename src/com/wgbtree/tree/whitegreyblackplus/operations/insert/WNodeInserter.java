package com.wgbtree.tree.whitegreyblackplus.operations.insert;

import com.wgbtree.tree.whitegreyblackplus.creator.WNodeCreator;
import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.whitegreyblackplus.prime.Primes.nextPrime;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WNodeInserter {

	public static <K extends Comparable<K>, T> WNode<K, T> insert(WNode<K, T> node, K key, Set<T> value, int keyHash, int order, int rank, AtomicReference<T> oldValue, boolean allowDuplicates) {
		if (isNull(node)) {
			return WNodeCreator.create(order, rank, key, value, allowDuplicates);
		}

		var entry = new SimpleEntry<>(key, value);
		var entries = node.getEntries();

		var leakEntry = new AtomicReference<Map.Entry<K, Set<T>>>();
		entries.add(entry, leakEntry);

		var leakedEntry = leakEntry.get();
		if (isNull(leakedEntry)) {
			return node;
		}

		if (leakedEntry != entry) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = key.hashCode();
		}

		var gNodes = node.getGNodes();
		int i = Math.abs(keyHash) % rank;
		gNodes[i] = GNodeInserter.insert(gNodes[i], key, value, keyHash, order, nextPrime(rank), oldValue, allowDuplicates);

		return node;
	}
}
