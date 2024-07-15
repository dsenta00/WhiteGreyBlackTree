package com.wgbtree.tree.whitegreyblackplus.operations.rotator;

import com.wgbtree.tree.whitegreyblackplus.node.BNode;
import com.wgbtree.tree.whitegreyblackplus.node.GNode;
import com.wgbtree.tree.whitegreyblackplus.node.WNode;
import com.wgbtree.tree.whitegreyblackplus.operations.delete.BNodeRemover;
import com.wgbtree.tree.whitegreyblackplus.operations.delete.WNodeRemover;
import com.wgbtree.tree.whitegreyblackplus.operations.insert.BNodeInserter;
import com.wgbtree.tree.whitegreyblackplus.operations.insert.WNodeInserter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy.LARGEST;
import static com.wgbtree.tree.whitegreyblackplus.constants.LeakPolicy.SMALLEST;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GNodeRotator {

	public static <K extends Comparable<K>, T> void tryRotateLeft(GNode<K, T> node,
																  int order,
																  int rank,
																  boolean allowDuplicates) {
		if (node.setLeakPolicy() == LARGEST) {
			/*
			 * Since keys are sorted ascending, the greatest key is set to leak right
			 * Hence, we won't do any rotation since we don't need it
			 */
			return;
		}

		if (node.getCountRight() == 0) {
			/*
			 * If the right node is empty, we don't need and cannot rotate
			 */
			return;
		}

		var result = BNodeRemover.removeMin(node.getRight());
		node.setRight((BNode<K, T>) result.getNode());
		node.decCountRight();

		var leakedEntryStorage = new AtomicReference<Map.Entry<K, Set<T>>>();
		node.getEntries().add(result.getEntry(), leakedEntryStorage);

		var leakedEntry = leakedEntryStorage.get();
		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();
		var leftNode = WNodeInserter.insert(node.getLeft(), leakedKey, leakedValue, leakedKeyHash, order, rank, new AtomicReference<>(), allowDuplicates);
		node.setLeft(leftNode);
		node.incCountLeft();
	}

	public static <K extends Comparable<K>, T> void tryRotateRight(GNode<K, T> node,
																   int order,
																   int rank,
																   boolean allowDuplicates) {
		if (node.setLeakPolicy() == SMALLEST) {
			/*
			 * Since keys are sorted ascending, the smallest key is set to leak left
			 * Hence, we won't do any rotation since we don't need it
			 */
			return;
		}

		if (node.getCountLeft() == 0) {
			/*
			 * If the left node is empty, we don't need and cannot rotate
			 */
			return;
		}

		var result = WNodeRemover.removeMax(node.getLeft());
		node.setLeft((WNode<K, T>) result.getNode());
		node.decCountLeft();

		var leakedEntryStorage = new AtomicReference<Map.Entry<K, Set<T>>>();
		node.getEntries().add(result.getEntry(), leakedEntryStorage);

		var leakedEntry = leakedEntryStorage.get();
		K leakedKey = leakedEntry.getKey();
		Set<T> leakedValue = leakedEntry.getValue();
		int leakedKeyHash = isNull(leakedEntry.getKey()) ? 0 : leakedEntry.getKey().hashCode();
		var rightNode = BNodeInserter.insert(node.getRight(), leakedKey, leakedValue, leakedKeyHash, order, rank, new AtomicReference<>(), allowDuplicates);
		node.setRight(rightNode);
		node.incCountRight();
	}
}
