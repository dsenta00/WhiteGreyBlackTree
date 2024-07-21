package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.Black;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackCreator {

	public static <K extends Comparable<K>, T>
	Black<K, T> create(K key, Set<T> value, LevelInfo info) {
		var black = new Black<K, T>(info.getOrder(), info.getRank(), info.isDuplicatesAllowed());
		black.getEntries().add(new SimpleEntry<>(key, value));
		return black;
	}
}
