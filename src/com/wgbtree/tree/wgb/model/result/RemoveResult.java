package com.wgbtree.tree.wgb.model.result;

import com.wgbtree.tree.wgb.model.node.Node;
import lombok.*;

import java.util.Map.Entry;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveResult<K extends Comparable<K>, T> {
	private Node<K, T> node;
	private Entry<K, Set<T>> entry;

	public static <K extends Comparable<K>, T> RemoveResult<K, T> of(Node<K, T> node, Entry<K, Set<T>> entry) {
		return new RemoveResult<>(node, entry);
	}

	public static <K extends Comparable<K>, T> RemoveResult<K, T> empty() {
		return new RemoveResult<>();
	}

	public boolean isEmpty() {
		return entry == null;
	}
}
