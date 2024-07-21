package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.Grey;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyCreator {

	public static <K extends Comparable<K>, T>
	Grey<K, T> create(K key, Set<T> value, LevelInfo info) {
		var grey = new Grey<K, T>(info.getOrder(), info.isDuplicatesAllowed());
		grey.getEntries().add(new SimpleEntry<>(key, value));
		return grey;
	}
}
