package com.wgbtree.tree.whitegreyblackplus.operations.insert;

import com.wgbtree.tree.whitegreyblackplus.creator.BlackCreator;
import com.wgbtree.tree.whitegreyblackplus.node.Black;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackInserter {

	public static <K extends Comparable<K>, T>
	Black<K, T> insert(Black<K, T> black, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, LevelInfo info) {
		if (isNull(black)) {
			return BlackCreator.create(key, value, info);
		}

		var entry = new SimpleEntry<>(key, value);
		var entries = black.getEntries();

		var leakEntry = new AtomicReference<Entry<K, Set<T>>>();
		entries.add(entry, leakEntry);

		var leakedEntry = leakEntry.get();
		if (isNull(leakedEntry)) {
			return black;
		}

		if (leakedEntry != entry) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = key.hashCode();
		}

		var greys = black.getGreys();
		int i = Math.abs(keyHash) % info.getRank();
		greys[i] = GreyInserter.insert(greys[i], key, value, keyHash, oldValue, info.nextLevel());

		return black;
	}
}
