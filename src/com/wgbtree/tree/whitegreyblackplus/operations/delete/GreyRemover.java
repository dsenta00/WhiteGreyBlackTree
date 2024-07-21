package com.wgbtree.tree.whitegreyblackplus.operations.delete;

import com.wgbtree.tree.whitegreyblackplus.node.Black;
import com.wgbtree.tree.whitegreyblackplus.node.Grey;
import com.wgbtree.tree.whitegreyblackplus.node.White;
import com.wgbtree.tree.whitegreyblackplus.node.delete.RemoveResult;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Map.Entry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRemover {

	public static <K extends Comparable<K>, T>
	RemoveResult<K, T> remove(Grey<K, T> grey, K key, int keyHash) {
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
			removedEntry = removeFromRight(grey, key, keyHash);
		} else if (entries.firstEntry().getKey().compareTo(key) > 0) {
			removedEntry = removeFromLeft(grey, key, keyHash);
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
			int keyHash = key.hashCode();

			return remove(grey, key, keyHash);
		}

		RemoveResult<K, T> result;

		if (right instanceof Black<K, T> rightAsBlack) {
			result = BlackRemover.removeMax(rightAsBlack);
		} else if (right instanceof Grey<K, T> rightAsGrey) {
			result = removeMax(rightAsGrey);
		} else {
			throw new IllegalStateException("Right node is not black or grey");
		}

		grey.setRight(result.getNode());
		grey.decCountRight();

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
			int keyHash = key.hashCode();

			return remove(grey, key, keyHash);
		}

		RemoveResult<K, T> result;

		if (left instanceof White<K, T> leftAsWhite) {
			result = WhiteRemover.removeMin(leftAsWhite);
		} else if (left instanceof Grey<K, T> leftAsGrey) {
			result = removeMin(leftAsGrey);
		} else {
			throw new IllegalStateException("Left node is not white or grey");
		}

		grey.setLeft(result.getNode());
		grey.decCountLeft();

		return RemoveResult.of(grey, result.getEntry());
	}

	private static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeFromRight(@NonNull Grey<K, T> grey, K key, int keyHash) {
		var right = grey.getRight();

		if (right == null) {
			// No right node, nothing to delete, key doesn't exist
			return null;
		}

		RemoveResult<K, T> result;

		if (right instanceof Black<K, T> rightAsBlack) {
			result = BlackRemover.remove(rightAsBlack, key, keyHash);
		} else if (right instanceof Grey<K, T> rightAsGrey) {
			result = remove(rightAsGrey, key, keyHash);
		} else {
			throw new IllegalStateException("Right node is not black or grey");
		}

		grey.setRight(result.getNode());
		grey.decCountRight();

		return result.getEntry();
	}

	private static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeFromLeft(@NonNull Grey<K, T> grey, K key, int keyHash) {
		var left = grey.getLeft();

		if (left == null) {
			// No left node, nothing to delete, key doesn't exist
			return null;
		}

		RemoveResult<K, T> result;

		if (left instanceof White<K, T> leftAsWhite) {
			result = WhiteRemover.remove(leftAsWhite, key, keyHash);
		} else if (left instanceof Grey<K, T> leftAsGrey) {
			result = remove(leftAsGrey, key, keyHash);
		} else {
			throw new IllegalStateException("Left node is not white or grey");
		}

		grey.setLeft(result.getNode());
		grey.decCountLeft();

		return result.getEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeMaxFromLeft(@NonNull Grey<K, T> grey) {
		var left = grey.getLeft();

		if (left == null) {
			throw new IllegalStateException("Left node is null");
		}

		RemoveResult<K, T> maxResult;

		if (left instanceof White<K, T> leftAsWhite) {
			maxResult = WhiteRemover.removeMax(leftAsWhite);
		} else if (left instanceof Grey<K, T> leftAsGrey) {
			maxResult = removeMax(leftAsGrey);
		} else {
			throw new IllegalStateException("Left node is not white or grey");
		}

		if (maxResult.getEntry() == null) {
			throw new IllegalStateException("Max entry is null");
		}

		grey.setLeft(maxResult.getNode());
		grey.decCountLeft();

		return maxResult.getEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeMinFromRight(@NonNull Grey<K, T> grey) {
		var right = grey.getRight();

		if (right == null) {
			throw new IllegalStateException("Right node is null");
		}

		RemoveResult<K, T> minResult;

		if (right instanceof Black<K, T> rightAsBlack) {
			minResult = BlackRemover.removeMin(rightAsBlack);
		} else if (right instanceof Grey<K, T> rightAsGrey) {
			minResult = removeMin(rightAsGrey);
		} else {
			throw new IllegalStateException("Right node is not black or grey");
		}

		if (minResult.getEntry() == null) {
			throw new IllegalStateException("Min entry is null");
		}

		grey.setRight(minResult.getNode());
		grey.decCountRight();

		return minResult.getEntry();
	}
}
