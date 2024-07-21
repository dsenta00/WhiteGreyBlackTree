package com.wgbtree.tree.whitegreyblackplus.operations.rotator;

import com.wgbtree.tree.whitegreyblackplus.node.Grey;
import com.wgbtree.tree.whitegreyblackplus.node.LevelInfo;
import com.wgbtree.tree.whitegreyblackplus.operations.delete.GreyRemover;
import com.wgbtree.tree.whitegreyblackplus.operations.insert.GreyInserter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy.SMALLEST;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRotator {

	public static <K extends Comparable<K>, T>
	Grey<K, T> tryRotateLeft(Grey<K, T> grey, LevelInfo info) {
		if (grey.setLeakPolicy() == LARGEST) {
			/*
			 * Since keys are sorted ascending, the greatest key is set to leak right
			 * Hence, we won't do any rotation since we don't need it
			 */
			return grey;
		}

		if (grey.getCountRight() == 0) {
			/*
			 * If the right node is empty, we don't need and cannot rotate
			 */
			return grey;
		}

		var entry = GreyRemover.removeMinFromRight(grey);
		var leakedEntry = GreyInserter.insertHere(grey, entry);

		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();

		return GreyInserter.insertLeft(grey, leakedKey, leakedValue, leakedKeyHash, new AtomicReference<>(), info);
	}

	public static <K extends Comparable<K>, T>
	Grey<K, T> tryRotateRight(Grey<K, T> grey, LevelInfo info) {
		if (grey.setLeakPolicy() == SMALLEST) {
			/*
			 * Since keys are sorted ascending, the smallest key is set to leak left
			 * Hence, we won't do any rotation since we don't need it
			 */
			return grey;
		}

		if (grey.getCountLeft() == 0) {
			/*
			 * If the left node is empty, we don't need and cannot rotate
			 */
			return grey;
		}

		var entry = GreyRemover.removeMaxFromLeft(grey);
		var leakedEntry = GreyInserter.insertHere(grey, entry);

		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();

		return GreyInserter.insertRight(grey, leakedKey, leakedValue, leakedKeyHash,new AtomicReference<>(), info);
	}
}
