package com.wgbtree.tree.whitegreyblackplus.operations.insert;

import com.wgbtree.tree.whitegreyblackplus.creator.WhiteCreator;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import com.wgbtree.tree.whitegreyblackplus.node.White;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteInserter {

	public static <K extends Comparable<K>, T>
	White<K, T> insert(White<K, T> white, K key, Set<T> value, int keyHash, AtomicReference<T> oldValue, LevelInfo info) {
		if (isNull(white)) {
			return WhiteCreator.create(key, value, info);
		}

		var entry = new SimpleEntry<>(key, value);
		var entries = white.getEntries();

		var leakEntry = new AtomicReference<Entry<K, Set<T>>>();
		entries.add(entry, leakEntry);

		var leakedEntry = leakEntry.get();
		if (isNull(leakedEntry)) {
			return white;
		}

		if (leakedEntry != entry) {
			key = leakedEntry.getKey();
			value = leakedEntry.getValue();
			keyHash = key.hashCode();
		}

		var greys = white.getGreys();
		int i = Math.abs(keyHash) % info.getRank();
		greys[i] = GreyInserter.insert(greys[i], key, value, keyHash, oldValue, info.nextLevel());

		return white;
	}
}
