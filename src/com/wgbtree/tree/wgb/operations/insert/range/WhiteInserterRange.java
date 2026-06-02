package com.wgbtree.tree.wgb.operations.insert.range;

import com.wgbtree.tree.wgb.creator.WhiteCreator;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.white.White;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteInserterRange {

	public static <K extends Comparable<K>, T>
	White<K, T> insert(White<K, T> white, K key, Set<T> value, AtomicReference<T> oldValue, TreeConfig config) {
		if (isNull(white)) {
			return WhiteCreator.createRange(key, value, config);
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
		}

		var greys = white.getGreys();
		greys[0] = GreyInserterRange.insert(greys[0], key, value, oldValue, config.nextRank());

		return white;
	}
}
