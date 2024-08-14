package com.wgbtree.tree.wgb.operations.rotate.straight;

import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.operations.delete.straight.GreyRemoverStraight;
import com.wgbtree.tree.wgb.operations.insert.straight.GreyInserterStraight;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRotatorStraight {


	public static <K extends Comparable<K>, T>
	Grey<K, T> tryRotateLeft(Grey<K, T> grey, TreeConfig config) {
		if (!config.getBalanced()) {
			/*
			 * The tree is not set to be balanced,
			 * We don't need and cannot rotate
			 */
			return grey;
		}

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

		var entry = GreyRemoverStraight.removeMinFromRight(grey);
		var leakedEntry = GreyInserterStraight.insertHere(grey, entry);

		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();

		return GreyInserterStraight.insertLeft(grey, leakedKey, leakedValue, leakedKeyHash, new AtomicReference<>(), config);
	}

	public static <K extends Comparable<K>, T>
	Grey<K, T> tryRotateRight(Grey<K, T> grey, TreeConfig config) {
		if (!config.getBalanced()) {
			/*
			 * The tree is not set to be balanced,
			 * We don't need and cannot rotate
			 */
			return grey;
		}

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

		var entry = GreyRemoverStraight.removeMaxFromLeft(grey);
		var leakedEntry = GreyInserterStraight.insertHere(grey, entry);

		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();

		return GreyInserterStraight.insertRight(grey, leakedKey, leakedValue, leakedKeyHash,new AtomicReference<>(), config);
	}
}
