package com.wgbtree.tree.wgb.operations.delete.mersenne;

import com.wgbtree.tree.wgb.calculator.MersenneCalculator;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.get.mersenne.GreyGetterMersenne;
import com.wgbtree.tree.wgb.prime.Primes;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteRemoverMersenne {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(White<K, T> white, K key, int keyHash) {
		if (white == null) {
			return RemoveResult.empty();
		}

		var entries = white.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		// Try to remove from the white node
		var removedEntry = entries.remove(key);

		if (removedEntry != null) {
			suckMinFromGreyNodes(white);
		} else {
			var lastEntry = entries.lastEntry();
			if (lastEntry != null && lastEntry.getKey().compareTo(key) < 0) {
				// Entry is not in the white node
				// Try to remove from the grey nodes
				var greys = white.getGreys();
				int i = MersenneCalculator.mod(keyHash, white.getCapacity(), Primes.mersenneExp(white.getCapacity()));
				var result = GreyRemoverMersenne.remove(greys[i], key, keyHash);
				greys[i] = (Grey<K, T>) result.getNode();

				removedEntry = result.getEntry();
			}
		}

		if (entries.isEmpty()) {
			white = null;
		}

		return RemoveResult.of(white, removedEntry);
	}

	public static <T, K extends Comparable<K>>
	RemoveResult<K, T> removeMax(White<K, T> white) {
		if (white == null) {
			return RemoveResult.empty();
		}

		var entries = white.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var maxResult = GreyGetterMersenne.getMax(white.getGreys());
		Map.Entry<K, Set<T>> removedEntry = null;
		if (maxResult.isEmpty()) {
			// No grey nodes, remove maximum from the white node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			var greys = white.getGreys();
			int i = maxResult.getIndex();
			K key = maxResult.getEntry().getKey();
			int keyHash = key.hashCode();
			var result = GreyRemoverMersenne.remove(greys[i], key, keyHash);
			greys[i] = (Grey<K, T>) result.getNode();
			removedEntry = result.getEntry();
		}

		if (entries.isEmpty()) {
			white = null;
		}

		return RemoveResult.of(white, removedEntry);
	}

	public static <T, K extends Comparable<K>>
	RemoveResult<K, T> removeMin(White<K, T> white) {
		if (white == null) {
			return RemoveResult.empty();
		}

		var entries = white.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var removedEntry = entries.remove(entries.firstEntry().getKey());
		suckMinFromGreyNodes(white);

		if (entries.isEmpty()) {
			white = null;
		}

		return RemoveResult.of(white, removedEntry);
	}

	private static <K extends Comparable<K>, T>
	void suckMinFromGreyNodes(White<K, T> white) {
		var greys = white.getGreys();

		var minResult = GreyGetterMersenne.getMin(greys);
		if (minResult.isEmpty()) {
			return;
		}

		int i = minResult.getIndex();
		K key = minResult.getEntry().getKey();
		int keyHash = key.hashCode();
		var result = GreyRemoverMersenne.remove(greys[i], key, keyHash);
		greys[i] = (Grey<K, T>) result.getNode();

		white.getEntries().add(result.getEntry());
	}
}
