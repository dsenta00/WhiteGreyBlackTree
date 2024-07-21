package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import com.wgbtree.tree.whitegreyblackplus.node.White;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteCreator {

	public static <K extends Comparable<K>, T>
	White<K, T> create(K key, Set<T> value, LevelInfo info) {
		var white = new White<K, T>(info.getOrder(), info.getRank(), info.isDuplicatesAllowed());
		white.getEntries().add(new SimpleEntry<>(key, value));
		return white;
	}
}
