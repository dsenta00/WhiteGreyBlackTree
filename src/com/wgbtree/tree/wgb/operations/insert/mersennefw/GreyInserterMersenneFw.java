package com.wgbtree.tree.wgb.operations.insert.mersennefw;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.creator.GreyCreator;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.black.FwMersenneBlack;
import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.FwMersenneWhite;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyInserterMersenneFw {

	public static <K extends Comparable<K>, T>
	Grey<K, T> insert(FwGrey<K, T> grey, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, TreeConfig config) {
		if (isNull(grey)) {
			return GreyCreator.createFw(key, value, config);
		}

		var entries = grey.getEntries();

		if (!entries.isFull()) {
			insertHere(grey, key, value);
			return grey;
		}

		if (entries.lastEntry().getKey().compareTo(key) < 0) {
			return insertRight(grey, key, value, keyHash, oldValue, config);
		}

		if (entries.firstEntry().getKey().compareTo(key) > 0) {
			return insertLeft(grey, key, value, keyHash, oldValue, config);
		}

		var leakPolicy = grey.setLeakPolicy();
		var leakedEntry = insertHere(grey, key, value);

		if (leakedEntry.getKey() != key) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = isNull(key) ? 0 : key.hashCode();
		}

		if (leakPolicy == LeakPolicy.SMALLEST) {
			return insertLeft(grey, key, value, keyHash, oldValue, config);
		} else {
			return insertRight(grey, key, value, keyHash, oldValue, config);
		}
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> insertHere(@NonNull FwGrey<K, T> grey,
								K key,
								Set<T> value) {
		var entry = new SimpleEntry<>(key, value);
		return insertHere(grey, entry);
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> insertHere(@NonNull FwGrey<K, T> grey,
								Entry<K, Set<T>> entry) {
		var leakedEntryStorage = new AtomicReference<Entry<K, Set<T>>>();
		grey.getEntries().add(entry, leakedEntryStorage);
		return leakedEntryStorage.get();
	}

	public static <K extends Comparable<K>, T>
	Grey<K, T> insertLeft(FwGrey<K, T> grey,
						  K key,
						  Set<T> value,
						  int keyHash,
						  AtomicReference<T> oldValue,
						  TreeConfig config) {
		var left = (FwMersenneWhite<K, T>) grey.getLeft();

		left = WhiteInserterMersenneFw.insert(left, key, value, keyHash, oldValue, config);

		grey.setLeft(left);
		if (oldValue.get() == null) {
			grey.incCountLeft();
		}

		return grey;
	}

	public static <K extends Comparable<K>, T>
	Grey<K, T> insertRight(FwGrey<K, T> grey,
						   K key,
						   Set<T> value,
						   int keyHash,
						   AtomicReference<T> oldValue,
						   TreeConfig config) {
		var right = (FwMersenneBlack<K, T>) grey.getRight();

		right = BlackInserterMersenneFw.insert(right, key, value, keyHash, oldValue, config);

		grey.setRight(right);
		if (oldValue.get() == null) {
			grey.incCountRight();
		}

		return grey;
	}
}
