package com.wgbtree.tree.wgb.operations.get.dec;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.comparator.EntryComparator;
import com.wgbtree.tree.wgb.model.node.Node;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import com.wgbtree.tree.wgb.model.node.white.White;
import com.wgbtree.tree.wgb.model.result.GreySearchResult;
import com.wgbtree.tree.wgb.operations.get.mersenne.BlackGetterMersenne;
import com.wgbtree.tree.wgb.operations.get.mersenne.WhiteGetterMersenne;
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
public final class GreyGetterDec {

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
				return WhiteGetterDec.getMin(leftAsWhite);
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
				return BlackGetterDec.getMax(rightAsBlack);
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
				WhiteGetterDec.getAllAsc(list, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllAsc(list, leftAsGrey);
			}
		}

		grey.getEntries().forEach(entry -> list.add(entry.getValue()));

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				BlackGetterDec.getAllAsc(list, rightAsBlack);
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
				WhiteGetterDec.getAllAsc(heapTree, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllAsc(heapTree, leftAsGrey);
			}
		}

		grey.getEntries().forEach(heapTree::push);

		var right = grey.getRight();

		if (right != null) {
			if (right instanceof Black<K, T> rightAsBlack) {
				BlackGetterDec.getAllAsc(heapTree, rightAsBlack);
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
				BlackGetterDec.getAllDesc(list, rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				getAllDesc(list, rightAsGrey);
			}
		}

		for (int i = grey.getEntries().size() - 1; i >= 0; i--) {
			list.add(grey.getEntries().get(i).getValue());
		}

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				WhiteGetterDec.getAllDesc(list, leftAsWhite);
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
				BlackGetterDec.getAllDesc(heapTree, rightAsBlack);
			} else if (right instanceof Grey<K, T> rightAsGrey) {
				getAllDesc(heapTree, rightAsGrey);
			}
		}

		grey.getEntries().forEach(heapTree::push);

		var left = grey.getLeft();

		if (left != null) {
			if (left instanceof White<K, T> leftAsWhite) {
				WhiteGetterDec.getAllDesc(heapTree, leftAsWhite);
			} else if (left instanceof Grey<K, T> leftAsGrey) {
				getAllDesc(heapTree, leftAsGrey);
			}
		}
	}

	public static <T, K extends Comparable<K>> List<Set<T>> getInAsc(Grey<K, T> grey, List<K> keys) {
		throw new UnsupportedOperationException("Not implemented yet");
	}


	public static <T, K extends Comparable<K>>
	List<Set<T>> getBetweenAsc(Grey<K, T> grey, K from, K to) {
		if (isNull(grey)) {
			return List.of();
		}

		if (from.compareTo(to) > 0) {
			return List.of();
		}

		List<Set<T>> list = new LinkedList<>();
		getBetweenAsc(list, grey, from, to);
		return list;
	}

	private static <T, K extends Comparable<K>>
	void getBetweenAsc(List<Set<T>> list, Grey<K, T> grey, K from, K to) {
		if (isNull(grey)) {
			return;
		}

		var entries = grey.getEntries();
		K firstKey = entries.firstEntry().getKey();
		int index = 0;

		if (firstKey.compareTo(from) > 0) {
			if (nonNull(grey.getLeft())) {
				if (grey.getLeft() instanceof White<K, T> leftAsWhite) {
					WhiteGetterMersenne.getBetweenAsc(list, leftAsWhite, from, to);
				} else if (grey.getLeft() instanceof Grey<K, T> leftAsGrey) {
					getBetweenAsc(list, leftAsGrey, from, to);
				}
			}
		} else {
			index = entries.searchClosest(from);
		}

		for (int i = index; i < entries.size(); i++) {
			if (entries.get(i).getKey().compareTo(to) >= 0) {
				return;
			}
			list.add(entries.get(i).getValue());
		}

		K lastKey = entries.lastEntry().getKey();

		if (lastKey.compareTo(to) < 0) {
			if (nonNull(grey.getRight())) {
				if (grey.getRight() instanceof Black<K, T> rightAsBlack) {
					BlackGetterMersenne.getBetweenAsc(list, rightAsBlack, from, to);
				} else if (grey.getRight() instanceof Grey<K, T> rightAsGrey) {
					getBetweenAsc(list, rightAsGrey, from, to);
				}
			}
		}
	}

	public static <K extends Comparable<K>, T>
	void getBetweenAsc(MinHeapTree<K, T> minHeapTree, Grey<K, T> grey, K from, K to) {
		if (isNull(grey)) {
			return;
		}

		var entries = grey.getEntries();
		K firstKey = grey.getEntries().firstEntry().getKey();
		int index = 0;

		if (firstKey.compareTo(from) > 0) {
			if (nonNull(grey.getLeft())) {
				if (grey.getLeft() instanceof White<K, T> leftAsWhite) {
					WhiteGetterMersenne.getBetweenAsc(minHeapTree, leftAsWhite, from, to);
				} else if (grey.getLeft() instanceof Grey<K, T> leftAsGrey) {
					getBetweenAsc(minHeapTree, leftAsGrey, from, to);
				}
			}
		} else {
			index = entries.searchClosest(from);
		}

		for (int i = index; i < grey.getEntries().size(); i++) {
			if (entries.get(i).getKey().compareTo(to) >= 0) {
				return;
			}
			minHeapTree.push(entries.get(i));
		}

		K lastKey = entries.lastEntry().getKey();
		if (lastKey.compareTo(to) < 0) {
			if (nonNull(grey.getRight())) {
				if (grey.getRight() instanceof Black<K, T> rightAsBlack) {
					BlackGetterMersenne.getBetweenAsc(minHeapTree, rightAsBlack, from, to);
				} else if (grey.getRight() instanceof Grey<K, T> rightAsGrey) {
					getBetweenAsc(minHeapTree, rightAsGrey, from, to);
				}
			}
		}
	}
}
