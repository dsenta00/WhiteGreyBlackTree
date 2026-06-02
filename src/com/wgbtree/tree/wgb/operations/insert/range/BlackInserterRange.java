package com.wgbtree.tree.wgb.operations.insert.range;

import com.wgbtree.tree.wgb.creator.BlackCreator;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.black.Black;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackInserterRange {

	public static <K extends Comparable<K>, T>
	Black<K, T> insert(Black<K, T> black, K key, Set<T> value, AtomicReference<T> oldValue, TreeConfig config) {
		if (isNull(black)) {
			return BlackCreator.createRange(key, value, config);
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
		}

		var greys = black.getGreys();
		greys[0] = GreyInserterRange.insert(greys[0], key, value, oldValue, config);

		return black;
	}
}
