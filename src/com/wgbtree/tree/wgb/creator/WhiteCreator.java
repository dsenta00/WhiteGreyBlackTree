package com.wgbtree.tree.wgb.creator;

import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.White;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteCreator {

	public static <K extends Comparable<K>, T>
	White<K, T> create(K key, Set<T> value, TreeConfig config) {
		var white = new White<K, T>(config.getOrder(), config.getRank(), config.getDuplicatesAllowed());
		white.getEntries().add(new SimpleEntry<>(key, value));
		return white;
	}
}
