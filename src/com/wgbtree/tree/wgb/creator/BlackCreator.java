package com.wgbtree.tree.wgb.creator;

import com.wgbtree.tree.wgb.model.node.Black;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackCreator {

	public static <K extends Comparable<K>, T>
	Black<K, T> create(K key, Set<T> value, TreeConfig config) {
		var black = new Black<K, T>(config.getOrder(), config.getRank(), config.getDuplicatesAllowed());
		black.getEntries().add(new SimpleEntry<>(key, value));
		return black;
	}
}
