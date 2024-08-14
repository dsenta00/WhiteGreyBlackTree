package com.wgbtree.tree.wgb.operations.delete.acc;

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
public final class GreyRemoverAcc {

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

	private static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeFromRight(@NonNull Grey<K, T> grey, K key, int keyHash) {
		var result = BlackRemoverAcc.remove((Black<K, T>) grey.getRight(), key, keyHash);

		grey.setRight(result.getNode());
		if (!result.isEmpty()) {
			grey.decCountRight();
		}

		return result.getEntry();
	}

	private static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeFromLeft(@NonNull Grey<K, T> grey, K key, int keyHash) {
		var result = WhiteRemoverAcc.remove((White<K, T>) grey.getLeft(), key, keyHash);

		grey.setLeft(result.getNode());
		if (!result.isEmpty()) {
			grey.decCountLeft();
		}

		return result.getEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeMaxFromLeft(@NonNull Grey<K, T> grey) {
		var maxResult = WhiteRemoverAcc.removeMax((White<K, T>) grey.getLeft());

		grey.setLeft(maxResult.getNode());
		if (!maxResult.isEmpty()) {
			grey.decCountLeft();
		}

		return maxResult.getEntry();
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> removeMinFromRight(@NonNull Grey<K, T> grey) {
		var minResult = BlackRemoverAcc.removeMin((Black<K, T>) grey.getRight());

		grey.setRight(minResult.getNode());
		if (!minResult.isEmpty()) {
			grey.decCountRight();
		}

		return minResult.getEntry();
	}
}
