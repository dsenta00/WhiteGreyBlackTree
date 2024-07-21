package com.wgbtree.tree.whitegreyblackplus.operations.get;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.whitegreyblackplus.comparator.EntryComparator;
import com.wgbtree.tree.whitegreyblackplus.node.Black;
import com.wgbtree.tree.whitegreyblackplus.node.Grey;
import com.wgbtree.tree.whitegreyblackplus.node.Node;
import com.wgbtree.tree.whitegreyblackplus.node.White;
import com.wgbtree.tree.whitegreyblackplus.node.delete.GreySearchResult;
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
public final class GreyGetter {

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

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				return WhiteGetter.getMin(leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				return getMin(leftAsGrey);
			}
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

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				return BlackGetter.getMax(rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				return getMax(rightAsGrey);
			}
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

	public static <T, K extends Comparable<K>> List<Set<T>> getAllAsc(Grey<K, T> grey) {
		List<Set<T>> list = new LinkedList<>();
		getAllAsc(list, grey);
		return list;
	}

	public static <T, K extends Comparable<K>> void getAllAsc(List<Set<T>> list, Grey<K, T> grey) {
		if (isNull(grey)) {
			return;
		}

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				WhiteGetter.getAllAsc(list, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllAsc(list, leftAsGrey);
			}
		}

		grey.getEntries().forEach(entry -> list.add(entry.getValue()));

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				BlackGetter.getAllAsc(list, rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				getAllAsc(list, rightAsGrey);
			}
		}
	}

	public static <T, K extends Comparable<K>> void getAllAsc(MinHeapTree<K, T> heapTree, Grey<K, T> grey) {
		if (isNull(grey)) {
			return;
		}

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				WhiteGetter.getAllAsc(heapTree, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllAsc(heapTree, leftAsGrey);
			}
		}

		grey.getEntries().forEach(heapTree::push);

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				BlackGetter.getAllAsc(heapTree, rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				getAllAsc(heapTree, rightAsGrey);
			}
		}
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getAllDesc(Grey<K, T> grey) {
		List<Set<T>> list = new LinkedList<>();
		getAllDesc(list, grey);
		return list;
	}

	public static <T, K extends Comparable<K>> void getAllDesc(List<Set<T>> list, Grey<K, T> grey) {
		if (isNull(grey)) {
			return;
		}

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				BlackGetter.getAllDesc(list, rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				getAllDesc(list, rightAsGrey);
			}
		}

		grey.getEntries().forEach(entry -> list.add(entry.getValue()));

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				WhiteGetter.getAllDesc(list, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllDesc(list, leftAsGrey);
			}
		}
	}

	public static <K extends Comparable<K>, T> void getAllDesc(MaxHeapTree<K, T> heapTree, Grey<K, T> grey) {
		if (isNull(grey)) {
			return;
		}

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				BlackGetter.getAllDesc(heapTree, rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				getAllDesc(heapTree, rightAsGrey);
			}
		}

		grey.getEntries().forEach(heapTree::push);

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				WhiteGetter.getAllDesc(heapTree, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllDesc(heapTree, leftAsGrey);
			}
		}
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getInAsc(Grey<K, T> grey, List<K> keys) {
		if (grey == null || keys == null || keys.isEmpty()) {
			return List.of();
		}

		if (keys.size() == 1) {
			K key = keys.remove(0);
			int keyHash = key == null ? 0 : key.hashCode();
			var result = get(grey, key, keyHash);

			return result.isEmpty() ? List.of() : List.of(result);
		}

		while (!keys.isEmpty()) {
			K firstKey = keys.remove(0);
			var minEntry = grey.getEntries().firstEntry();

			if (minEntry.getKey() == null) {
				if (firstKey == null) {
					// TODO Finish this
				}
			}

		}

		throw new UnsupportedOperationException("Not implemented yet");
	}
}
