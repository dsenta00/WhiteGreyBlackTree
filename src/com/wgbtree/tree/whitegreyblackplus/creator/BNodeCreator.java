package com.wgbtree.tree.whitegreyblackplus.creator;

import com.wgbtree.tree.whitegreyblackplus.node.BNode;
import lombok.NoArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BNodeCreator {

	public static <K extends Comparable<K>, T> BNode<K, T> create(int order, int rank, K key, Set<T> value, boolean allowDuplicates) {
		var bnode = new BNode<K, T>(order, rank, allowDuplicates);
		bnode.getEntries().add(new SimpleEntry<>(key, value), new AtomicReference<>());
		return bnode;
	}
}
