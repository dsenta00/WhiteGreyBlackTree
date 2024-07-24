package com.wgbtree.tree.wgb.creator;

import com.wgbtree.tree.wgb.model.node.Grey;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyCreator {

	public static <K extends Comparable<K>, T>
	Grey<K, T> create(K key, Set<T> value, TreeConfig config) {
		var grey = new Grey<K, T>(config.getOrder(), config.getDuplicatesAllowed());
		grey.getEntries().add(new SimpleEntry<>(key, value));
		return grey;
	}
}
