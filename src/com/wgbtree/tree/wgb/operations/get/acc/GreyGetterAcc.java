package com.wgbtree.tree.wgb.operations.get.acc;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.comparator.EntryComparator;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.Node;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.model.result.GreySearchResult;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class GreyGetterAcc {

	public static <K extends Comparable<K>, T>
	GreySearchResult<K, T> getMin(Grey<K, T>[] greys) {
		if (isNull(greys) || greys.length == 0) {
			// arrays is null or empty, return empty result
			return GreySearchResult.empty();
		}

		return IntStream.range(0, greys.length)
				.mapToObj(i -> GreySearchResult.of(i, getMin(greys[i])))
				.filter(GreySearchResult::isPresent)
				.min((e1, e2) -> EntryComparator.compare(e1.getEntry(), e2.getEntry()))
				.orElse(GreySearchResult.empty());
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMin(Grey<K, T> grey) {
		if (isNull(grey)) {
			// Node is empty, return null
			return null;
		}

		var left = (White<K, T>) grey.getLeft();

		if (left != null) {
			return WhiteGetterAcc.getMin(left);
		}

		var entries = grey.getEntries();

		return entries.isEmpty() ? null : entries.firstEntry();
	}

	public static <K extends Comparable<K>, T>
	GreySearchResult<K, T> getMax(Grey<K, T>[] greys) {
		if (isNull(greys) || greys.length == 0) {
			// arrays is null or empty, return empty result
			return GreySearchResult.empty();
		}

		return IntStream.range(0, greys.length)
				.mapToObj(i -> GreySearchResult.of(i, getMax(greys[i])))
				.filter(GreySearchResult::isPresent)
				.max((e1, e2) -> EntryComparator.compare(e1.getEntry(), e2.getEntry()))
				.orElse(GreySearchResult.empty());
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMax(Grey<K, T> grey) {
		if (isNull(grey)) {
			// Node is empty, return null
			return null;
		}

		var right = (Black<K, T>) grey.getRight();

		if (right != null) {
			return BlackGetterAcc.getMax(right);
		}

		var entries = grey.getEntries();

		return entries.isEmpty() ? null : entries.lastEntry();
	}

	public static <K extends Comparable<K>, T>
	Set<T> get(Grey<K, T> grey, K key, int keyHash) {
		if (isNull(grey) || grey.getEntries().isEmpty()) {
			// Node is empty, return empty set
			return Set.of();
		}

		for (Node<K, T> n = grey; nonNull(n); n = n.nextNode(key, keyHash)) {
			var optionalEntry = n.getEntries().find(key);

			if (optionalEntry.isPresent()) {
				return optionalEntry.get().getValue();
			}
		}

		return Set.of();
	}

	public static <T, K extends Comparable<K>>
	List<Entry<K, Set<T>>> getAllAsc(Grey<K, T> grey) {
		List<Entry<K, Set<T>>> list = new LinkedList<>();
		getAllAsc(list, grey);
		return list;
	}

	public static <T, K extends Comparable<K>>
	void getAllAsc(List<Entry<K, Set<T>>> list, Grey<K, T> grey) {
		if (isNull(grey)) {
			return;
		}

		WhiteGetterAcc.getAllAsc(list, (White<K, T>) grey.getLeft());
        list.addAll(grey.getEntries());
		BlackGetterAcc.getAllAsc(list, (Black<K, T>) grey.getRight());
	}

	public static <T, K extends Comparable<K>>
	List<Entry<K, Set<T>>> getAllDesc(Grey<K, T> grey) {
		List<Entry<K, Set<T>>> list = new LinkedList<>();
		getAllDesc(list, grey);
		return list;
	}

	public static <T, K extends Comparable<K>>
	void getAllDesc(List<Entry<K, Set<T>>> list, Grey<K, T> grey) {
		if (isNull(grey)) {
			return;
		}

		BlackGetterAcc.getAllDesc(list, (Black<K, T>) grey.getRight());

		for (int i = grey.getEntries().size() - 1; i >= 0; i--) {
			list.add(grey.getEntries().get(i));
		}

		WhiteGetterAcc.getAllDesc(list, (White<K, T>) grey.getLeft());
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getInAsc(Grey<K, T> grey, List<K> keys) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
