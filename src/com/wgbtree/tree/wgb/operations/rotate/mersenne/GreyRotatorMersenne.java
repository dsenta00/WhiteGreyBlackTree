package com.wgbtree.tree.wgb.operations.rotate.mersenne;

import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.operations.delete.mersenne.GreyRemoverMersenne;
import com.wgbtree.tree.wgb.operations.insert.mersenne.GreyInserterMersenne;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.wgb.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.wgb.constants.LeakPolicy.SMALLEST;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyRotatorMersenne {

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

		var entry = GreyRemoverMersenne.removeMinFromRight(grey);
		var leakedEntry = GreyInserterMersenne.insertHere(grey, entry);

		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();

		return GreyInserterMersenne.insertLeft(grey, leakedKey, leakedValue, leakedKeyHash, new AtomicReference<>(), config);
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

		var entry = GreyRemoverMersenne.removeMaxFromLeft(grey);
		var leakedEntry = GreyInserterMersenne.insertHere(grey, entry);

		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();

		return GreyInserterMersenne.insertRight(grey, leakedKey, leakedValue, leakedKeyHash,new AtomicReference<>(), config);
	}
}
