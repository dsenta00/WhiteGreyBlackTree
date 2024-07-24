package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.wgb.model.node.Black;
import com.wgbtree.tree.wgb.model.node.Grey;
import com.wgbtree.tree.wgb.model.node.White;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyHandler {

	public static <K extends Comparable<K>, T> int size(Grey<K, T> node) {
		if (isNull(node)) {
			return 0;
		}

		return node.getEntries().size() + node.getCountLeft() + node.getCountRight();
	}

	public static <K extends Comparable<K>, T> int depth(Grey<K, T> grey) {
		if (isNull(grey)) {
			return 0;
		}

		int depthLeft = depthLeft(grey);
		int depthRight = depthRight(grey);

		return Math.max(depthLeft, depthRight) + 1;
	}

	private static <K extends Comparable<K>, T> int depthLeft(@NonNull Grey<K, T> grey) {
		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				return WhiteHandler.depth(leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				return depth(leftAsGrey);
			}
		}

		return 0;
	}

	private static <K extends Comparable<K>, T> int depthRight(@NonNull Grey<K, T> grey) {
		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsWhite) {
				return BlackHandler.depth(rightAsWhite);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				return depth(rightAsGrey);
			}
		}

		return 0;
	}
}
