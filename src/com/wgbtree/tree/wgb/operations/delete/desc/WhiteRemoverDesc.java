package com.wgbtree.tree.wgb.operations.delete.desc;

import com.wgbtree.tree.wgb.model.node.Grey;
import com.wgbtree.tree.wgb.model.node.White;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.get.desc.GreyGetterDesc;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteRemoverDesc {

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
				int i = Math.abs(keyHash) % white.getCapacity();
				var result = GreyRemoverDesc.remove(greys[i], key, keyHash);
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

		var maxResult = GreyGetterDesc.getMax(white.getGreys());
		Map.Entry<K, Set<T>> removedEntry = null;
		if (maxResult.isEmpty()) {
			// No grey nodes, remove maximum from the white node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			var greys = white.getGreys();
			int i = maxResult.getIndex();
			K key = maxResult.getEntry().getKey();
			int keyHash = key.hashCode();
			var result = GreyRemoverDesc.remove(greys[i], key, keyHash);
			greys[i] = (Grey<K, T>) result.getNode();

			if (result.getEntry() == null) {
				throw new IllegalStateException("Entry not found in the grey node");
			}
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

		var minResult = GreyGetterDesc.getMin(greys);
		if (minResult.isEmpty()) {
			return;
		}

		int i = minResult.getIndex();
		K key = minResult.getEntry().getKey();
		int keyHash = key.hashCode();
		var result = GreyRemoverDesc.remove(greys[i], key, keyHash);
		greys[i] = (Grey<K, T>) result.getNode();

		white.getEntries().add(result.getEntry());
	}
}
