package com.wgbtree.tree.wgb.operations.delete.range;

import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import com.wgbtree.tree.wgb.operations.get.range.GreyGetterRange;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackRemoverRange {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(Black<K, T> black, K key) {
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
			suckMaxFromGreyNode(black);
		} else {
			var lastEntry = entries.lastEntry();
			if (lastEntry != null && (key == null || lastEntry.getKey().compareTo(key) > 0)) {
				// Entry is not in the black node
				// Try to remove from the grey nodes
				var greys = black.getGreys();
				var result = GreyRemoverRange.remove(greys[0], key);
				greys[0] = (Grey<K, T>) result.getNode();

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
		suckMaxFromGreyNode(black);

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

		var greys = black.getGreys();
		var entry = GreyGetterRange.getMin(greys[0]);
		Map.Entry<K, Set<T>> removedEntry;
		if (entry == null) {
			// No grey nodes, remove minimum from the black node
			removedEntry = entries.remove(entries.lastEntry().getKey());
		} else {
			K key = entry.getKey();
			var result = GreyRemoverRange.remove(greys[0], key);
			greys[0] = (Grey<K, T>) result.getNode();
			removedEntry = result.getEntry();
		}

		if (entries.isEmpty()) {
			black = null;
		}

		return RemoveResult.of(black, removedEntry);
	}

	private static <K extends Comparable<K>, T>
	void suckMaxFromGreyNode(Black<K, T> black) {
		var greys = black.getGreys();
		var result = GreyRemoverRange.removeMax(greys[0]);
		if (result.isEmpty()) {
			return;
		}
		greys[0] = (Grey<K, T>) result.getNode();
		black.getEntries().add(result.getEntry());
	}
}
