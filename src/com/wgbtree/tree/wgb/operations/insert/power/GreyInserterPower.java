package com.wgbtree.tree.wgb.operations.insert.power;

import com.wgbtree.tree.wgb.constants.LeakPolicy;
import com.wgbtree.tree.wgb.creator.GreyCreator;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.operations.rotate.power.GreyRotatorPower;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyInserterPower {

	public static <K extends Comparable<K>, T>
	Grey<K, T> insert(Grey<K, T> grey, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, TreeConfig config) {
		if (isNull(grey)) {
			return GreyCreator.create(key, value, config);
		}

		var entries = grey.getEntries();

		if (!entries.isFull()) {
			insertHere(grey, key, value);
			return grey;
		}

		if (entries.lastEntry().getKey().compareTo(key) < 0) {
			insertRight(grey, key, value, keyHash, oldValue, config);
			return GreyRotatorPower.tryRotateLeft(grey, config);
		}

		if (entries.firstEntry().getKey().compareTo(key) > 0) {
			insertLeft(grey, key, value, keyHash, oldValue, config);
			return GreyRotatorPower.tryRotateRight(grey, config);
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
	Entry<K, Set<T>> insertHere(@NonNull Grey<K, T> grey,
								K key,
								Set<T> value) {
		var entry = new SimpleEntry<>(key, value);
		return insertHere(grey, entry);
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> insertHere(@NonNull Grey<K, T> grey,
								Entry<K, Set<T>> entry) {
		var leakedEntryStorage = new AtomicReference<Entry<K, Set<T>>>();
		grey.getEntries().add(entry, leakedEntryStorage);
		return leakedEntryStorage.get();
	}

	public static <K extends Comparable<K>, T>
	Grey<K, T> insertLeft(Grey<K, T> grey,
						  K key,
						  Set<T> value,
						  int keyHash,
						  AtomicReference<T> oldValue,
						  TreeConfig config) {
		var left = WhiteInserterPower.insert((White<K, T>) grey.getLeft(), key, value, keyHash, oldValue, config);

		grey.setLeft(left);
		if (oldValue.get() == null) {
			grey.incCountLeft();
		}

		return grey;
	}

	public static <K extends Comparable<K>, T>
	Grey<K, T> insertRight(Grey<K, T> grey,
						   K key,
						   Set<T> value,
						   int keyHash,
						   AtomicReference<T> oldValue,
						   TreeConfig config) {
		var right = BlackInserterPower.insert((Black<K, T>) grey.getRight(), key, value, keyHash, oldValue, config);

		grey.setRight(right);
		if (oldValue.get() == null) {
			grey.incCountRight();
		}

		return grey;
	}
}
