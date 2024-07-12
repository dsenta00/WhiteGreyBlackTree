package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GNodeCreator {

	public static <K extends Comparable<K>, T> GNode<K, T> create(int order, K key, Set<T> value, boolean allowDuplicates) {
		var gnode = new GNode<K, T>(order, allowDuplicates);
		gnode.getEntries().add(new SimpleEntry<>(key, value), new AtomicReference<>());
		return gnode;
	}
}
