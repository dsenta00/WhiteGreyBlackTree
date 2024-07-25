package com.wgbtree.tree.wgb.operations.delete.asc;

import com.wgbtree.tree.wgb.model.node.Black;
import com.wgbtree.tree.wgb.model.node.Grey;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.get.asc.GreyGetterAsc;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackRemoverAsc {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(Black<K, T> black, K key, int keyHash) {
		if (black == null) {
			return RemoveResult.empty();
		}

		var entries = black.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		// Try to remove from the black node
		var removedEntry = entries.remove(key);

		if (removedEntry != null) {
			suckMaxFromGreyNodes(black);
		} else {
			var lastEntry = entries.lastEntry();
			if (lastEntry != null && (key == null || lastEntry.getKey().compareTo(key) > 0)) {
				// Entry is not in the black node
				// Try to remove from the grey nodes
				var greys = black.getGreys();
				int i = Math.abs(keyHash) % black.getCapacity();
				var result = GreyRemoverAsc.remove(greys[i], key, keyHash);
				greys[i] = (Grey<K, T>) result.getNode();

				removedEntry = result.getEntry();
			}
		}

		if (entries.isEmpty()) {
			black = null;
		}

		return RemoveResult.of(black, removedEntry);
	}

	public static <T, K extends Comparable<K>>
	RemoveResult<K, T> removeMax(Black<K, T> black) {
		if (black == null) {
			return RemoveResult.empty();
		}

		var entries = black.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var removedEntry = entries.remove(entries.firstEntry().getKey());
		suckMaxFromGreyNodes(black);

		if (entries.isEmpty()) {
			black = null;
		}

		return RemoveResult.of(black, removedEntry);
	}

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> removeMin(Black<K, T> black) {
		if (black == null) {
			return RemoveResult.empty();
		}

		var entries = black.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		var minResult = GreyGetterAsc.getMin(black.getGreys());
		Map.Entry<K, Set<T>> removedEntry;
		if (minResult.isEmpty()) {
			// No grey nodes, remove minimum from the black node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			var greys = black.getGreys();
			int i = minResult.getIndex();
			K key = minResult.getEntry().getKey();
			int keyHash = key.hashCode();
			var result = GreyRemoverAsc.remove(greys[i], key, keyHash);
			greys[i] = (Grey<K, T>) result.getNode();
			removedEntry = result.getEntry();
		}

		if (entries.isEmpty()) {
			black = null;
		}

		return RemoveResult.of(black, removedEntry);
	}

	private static <K extends Comparable<K>, T>
	void suckMaxFromGreyNodes(Black<K, T> black) {
		var greys = black.getGreys();

		var maxResult = GreyGetterAsc.getMax(greys);
		if (maxResult.isEmpty()) {
			return;
		}

		int i = maxResult.getIndex();
		K key = maxResult.getEntry().getKey();
		int keyHash = key.hashCode();
		var result = GreyRemoverAsc.remove(greys[i], key, keyHash);
		greys[i] = (Grey<K, T>) result.getNode();
		black.getEntries().add(result.getEntry());
	}
}
