package com.wgbtree.tree.wgb.operations.delete.range;

import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.get.range.GreyGetterRange;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteRemoverRange {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(White<K, T> white, K key) {
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
			suckMinFromGreyNode(white);
		} else {
			var lastEntry = entries.lastEntry();
			if (lastEntry != null && lastEntry.getKey().compareTo(key) < 0) {
				// Entry is not in the white node
				// Try to remove from the grey nodes
				var greys = white.getGreys();
				var result = GreyRemoverRange.remove(greys[0], key);
				greys[0] = (Grey<K, T>) result.getNode();

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

		var greys = white.getGreys();
		var entry = GreyGetterRange.getMax(greys[0]);
		Map.Entry<K, Set<T>> removedEntry = null;
		if (entry == null) {
			// No grey nodes, remove maximum from the white node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			K key = entry.getKey();
			var result = GreyRemoverRange.remove(greys[0], key);
			greys[0] = (Grey<K, T>) result.getNode();
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
		suckMinFromGreyNode(white);

		if (entries.isEmpty()) {
			white = null;
		}

		return RemoveResult.of(white, removedEntry);
	}

	private static <K extends Comparable<K>, T>
	void suckMinFromGreyNode(White<K, T> white) {
		var greys = white.getGreys();
		var result = GreyRemoverRange.removeMin(greys[0]);
		if (result.isEmpty()) {
			return;
		}
		greys[0] = (Grey<K, T>) result.getNode();
		white.getEntries().add(result.getEntry());
	}
}
