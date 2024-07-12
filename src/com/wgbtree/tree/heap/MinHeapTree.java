package com.wgbtree.tree.heap;

import java.util.Map.Entry;
import java.util.Set;

public class MinHeapTree<K extends Comparable<K>, T> extends HeapTree<K, T> {

	@Override
	public void push(Entry<K, Set<T>> entry) {
		if (root == null) {
			root = new HeapNode<>(entry);
			return;
		}

		HeapNode<K, T> node = root;
		while (true) {
			if (entry.getKey().compareTo(node.entry.getKey()) < 0) {
				if (node.left == null) {
					node.left = new HeapNode<>(entry);
					return;
				}
				node = node.left;
			} else {
				if (node.right == null) {
					node.right = new HeapNode<>(entry);
					return;
				}
				node = node.right;
			}
		}
	}

	@Override
	public Entry<K, Set<T>> pop() {
		if (root == null) {
			return null;
		}

		HeapNode<K, T> node = root;
		HeapNode<K, T> parent = null;
		while (node.left != null) {
			parent = node;
			node = node.left;
		}

		if (parent == null) {
			root = node.right;
		} else {
			parent.left = node.right;
		}

		return node.entry;
	}
}
