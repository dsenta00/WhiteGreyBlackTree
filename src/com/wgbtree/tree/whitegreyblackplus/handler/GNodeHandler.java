package com.wgbtree.tree.whitegreyblackplus.handler;

import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import lombok.NoArgsConstructor;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GNodeHandler {

	public static <K extends Comparable<K>, T> int size(GNode<K, T> node) {
		if (isNull(node)) {
			return 0;
		}

		return node.getEntries().size() + node.getCountLeft() + node.getCountRight();
	}

	public static <K extends Comparable<K>, T> int depth(GNode<K, T> gNode) {
		if (isNull(gNode)) {
			return 0;
		}

		return Math.max(WNodeHandler.depth(gNode.getLeft()), BNodeHandler.depth(gNode.getRight())) + 1;
	}
}
