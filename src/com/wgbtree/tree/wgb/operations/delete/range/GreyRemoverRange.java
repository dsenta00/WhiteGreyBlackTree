package com.wgbtree.tree.wgb.operations.delete.range;

import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.model.result.RemoveResult;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map.Entry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRemoverRange {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(Grey<K, T> grey, K key) {
		if (grey == null) {
			return RemoveResult.empty();
		}

		var entries = grey.getEntries();

		if (entries.isEmpty()) {
			return RemoveResult.empty();
		}

		// Try to remove from the grey node
		var removedEntry = entries.remove(key);

		if (removedEntry != null) {
			switch (grey.getMorePopulatedDirection()) {
				case LEFT -> {
					var maxEntry = removeMaxFromLeft(grey);
					grey.getEntries().add(maxEntry);
				}
				case RIGHT -> {
					var minEntry = removeMinFromRight(grey);
					grey.getEntries().add(minEntry);
				}
			}
		} else if (entries.lastEntry().getKey().compareTo(key) < 0) {
			removedEntry = removeFromRight(grey, key);
		} else if (entries.firstEntry().getKey().compareTo(key) > 0) {
			removedEntry = removeFromLeft(grey, key);
		}

		if (entries.isEmpty()) {
			grey = null;
		}

		return RemoveResult.of(grey, removedEntry);
	}

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> removeMax(Grey<K, T> grey) {
		if (grey == null) {
			return RemoveResult.empty();
		}

		var right = grey.getRight();

		if (right == null) {
			var entries = grey.getEntries();
			if (entries.isEmpty()) {
				return RemoveResult.empty();
			}

			var maxEntry = entries.lastEntry();
			K key = maxEntry.getKey();

			return remove(grey, key);
		}

		var result = BlackRemoverRange.removeMax((Black<K, T>) right);

		grey.setRight(result.getNode());

		if (!result.isEmpty()) {
			grey.decCountRight();
		}

		return RemoveResult.of(grey, result.getEntry());
	}

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> removeMin(Grey<K, T> grey) {
		if (grey == null) {
			return RemoveResult.empty();
		}

		var left = grey.getLeft();

		if (left == null) {
			var entries = grey.getEntries();
			if (entries.isEmpty()) {
				return RemoveResult.empty();
			}

			var minEntry = entries.firstEntry();
			K key = minEntry.getKey();

			return remove(grey, key);
		}

		var result = WhiteRemoverRange.removeMin((White<K, T>) left);

		grey.setLeft(result.getNode());

		if (!result.isEmpty()) {
			grey.decCountLeft();
		}

		return RemoveResult.of(grey, result.getEntry());
	}

	private static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeFromRight(@NonNull Grey<K, T> grey, K key) {
		var right = grey.getRight();

		if (right == null) {
			// No right node, nothing to delete, key doesn't exist
			return null;
		}

		var result = BlackRemoverRange.remove((Black<K, T>) right, key);

		grey.setRight(result.getNode());

		if (!result.isEmpty()) {
			grey.decCountRight();
		}

		return result.getEntry();
	}

	private static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeFromLeft(@NonNull Grey<K, T> grey, K key) {
		var left = grey.getLeft();

		if (left == null) {
			// No left node, nothing to delete, key doesn't exist
			return null;
		}

		var result = WhiteRemoverRange.remove((White<K, T>) left, key);

		grey.setLeft(result.getNode());

		if (!result.isEmpty()) {
			grey.decCountLeft();
		}

		return result.getEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeMaxFromLeft(@NonNull Grey<K, T> grey) {
		var left = grey.getLeft();

		if (left == null) {
			throw new IllegalStateException("Left node is null");
		}

		var maxResult = WhiteRemoverRange.removeMax((White<K, T>) left);

		grey.setLeft(maxResult.getNode());
		if (!maxResult.isEmpty()) {
			grey.decCountLeft();
		}

		return maxResult.getEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeMinFromRight(@NonNull Grey<K, T> grey) {
		var right = grey.getRight();

		if (right == null) {
			throw new IllegalStateException("Right node is null");
		}

		var minResult = BlackRemoverRange.removeMin((Black<K, T>) right);

		grey.setRight(minResult.getNode());
		if (!minResult.isEmpty()) {
			grey.decCountRight();
		}

		return minResult.getEntry();
	}
}
