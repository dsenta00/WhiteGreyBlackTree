package com.wgbtree.tree.whitegreyblackplus.handler;

import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WNodeHandler {

	public static <K extends Comparable<K>, T> int depth(WNode<K, T> node) {
		if (isNull(node)) {
			return 0;
		}

		return Arrays.stream(node.getGNodes())
				.map(GNodeHandler::depth)
				.max(Integer::compareTo)
				.orElse(0) + 1;
	}
}