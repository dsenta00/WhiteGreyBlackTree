package com.wgbtree.tree.whitegreyblackplus.operations.insert;

import com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy;
import com.wgbtree.tree.whitegreyblackplus.creator.BlackCreator;
import com.wgbtree.tree.whitegreyblackplus.creator.GreyCreator;
import com.wgbtree.tree.whitegreyblackplus.creator.WhiteCreator;
import com.wgbtree.tree.whitegreyblackplus.node.Black;
import com.wgbtree.tree.whitegreyblackplus.node.Grey;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import com.wgbtree.tree.whitegreyblackplus.node.White;
import com.wgbtree.tree.whitegreyblackplus.operations.rotator.GreyRotator;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyInserter {

	public static <K extends Comparable<K>, T>
	Grey<K, T> insert(Grey<K, T> grey, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, LevelInfo info) {
		if (isNull(grey)) {
			return GreyCreator.create(key, value, info);
		}

		var entries = grey.getEntries();

		if (!entries.isFull()) {
			insertHere(grey, key, value);
			return grey;
		}

		if (entries.lastEntry().getKey().compareTo(key) < 0) {
			insertRight(grey, key, value, keyHash, oldValue, info);
			return GreyRotator.tryRotateLeft(grey, info);
		}

		if (entries.firstEntry().getKey().compareTo(key) > 0) {
			insertLeft(grey, key, value, keyHash, oldValue, info);
			return GreyRotator.tryRotateRight(grey, info);
		}

		var leakPolicy = grey.setLeakPolicy();
		var leakedEntry = insertHere(grey, key, value);

		if (leakedEntry.getKey() != key) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = isNull(key) ? 0 : key.hashCode();
		}

		if (leakPolicy == LeakPolicy.SMALLEST) {
			return insertLeft(grey, key, value, keyHash, oldValue, info);
		} else {
			return insertRight(grey, key, value, keyHash, oldValue, info);
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
						  LevelInfo info) {

		var left = grey.getLeft();

		if (isNull(left)) {
			if (info.shouldHaveGreyChildren()) {
				left = GreyCreator.create(key, value, info);
			} else {
				left = WhiteCreator.create(key, value, info);
			}
		} else if (left instanceof Grey<K, T> leftAsGrey) {
			left = insert(leftAsGrey, key, value, keyHash, oldValue, info);
		} else if (left instanceof White<K, T> leftAsWhite) {
			left = WhiteInserter.insert(leftAsWhite, key, value, keyHash, oldValue, info);
		} else {
			throw new IllegalStateException("Left node is not white or grey");
		}

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
						   LevelInfo info) {
		var right = grey.getRight();

		if (isNull(right)) {
			if (info.shouldHaveGreyChildren()) {
				right = GreyCreator.create(key, value, info);
			} else {
				right = BlackCreator.create(key, value, info);
			}
		} else if (right instanceof Grey<K, T> rightAsGrey) {
			right = insert(rightAsGrey, key, value, keyHash, oldValue, info);
		} else if (right instanceof Black<K, T> rightAsBlack) {
			right = BlackInserter.insert(rightAsBlack, key, value, keyHash, oldValue, info);
		} else {
			throw new IllegalStateException("Right node is not black or grey");
		}

		grey.setRight(right);
		if (oldValue.get() == null) {
			grey.incCountRight();
		}

		return grey;
	}
}
