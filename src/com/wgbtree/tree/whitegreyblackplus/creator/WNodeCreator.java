package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import lombok.NoArgsConstructor;

import java.util.AbstractMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WNodeCreator {

	public static <K extends Comparable<K>, T> WNode<K, T> create(int order, int rank, K key, Set<T> value, boolean allowDuplicates) {
		var wNode = new WNode<K, T>(order, rank, allowDuplicates);
		wNode.getEntries().add(new AbstractMap.SimpleEntry<>(key, value), new AtomicReference<>());
		return wNode;
	}
}
