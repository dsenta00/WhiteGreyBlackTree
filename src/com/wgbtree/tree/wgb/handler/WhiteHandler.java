package com.wgbtree.tree.wgb.handler;

import com.wgbtree.tree.wgb.model.node.White;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class WhiteHandler {

	public static <K extends Comparable<K>, T> int depth(White<K, T> node) {
		if (isNull(node)) {
			return 0;
		}

		return Arrays.stream(node.getGreys())
				.map(GreyHandler::depth)
				.max(Integer::compareTo)
				.orElse(0) + 1;
	}
}