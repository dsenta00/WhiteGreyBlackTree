package com.wgbtree.tree.wgb.operations.get.mersennefw;

import com.wgbtree.tree.heap.MaxHeapTree;
import com.wgbtree.tree.heap.MinHeapTree;
import com.wgbtree.tree.wgb.handler.EntryHandler;
import com.wgbtree.tree.wgb.model.node.black.Black;
import com.wgbtree.tree.wgb.model.node.black.FwMersenneBlack;
import com.wgbtree.tree.wgb.operations.get.range.GreyGetterRange;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BlackGetterMersenneFw {

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMin(FwMersenneBlack<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		Entry<K, Set<T>> min = null;

		if (black.greyRoot != null) {
			min = GreyGetterMersenneFw.getMin(black.greyRoot);

			if (min == null) {
				return black.getEntries().lastEntry();
			}

			for (var grey = black.greyRoot.fwRight; grey != null; grey = grey.fwRight) {
				var newMin = GreyGetterMersenneFw.getMin(grey);
				if (newMin == null) {
					continue;
				}
				if (newMin.getKey().compareTo(min.getKey()) < 0) {
					min = newMin;
				}
			}
		} else {
			for (var grey : black.getGreys()) {
				var newMin = GreyGetterRange.getMin(grey);
				if (newMin == null) {
					continue;
				}
				if (min == null || newMin.getKey().compareTo(min.getKey()) < 0) {
					min = newMin;
				}
			}

			if (min == null) {
				return black.getEntries().lastEntry();
			}
		}

		return min;
	}

	public static <K extends Comparable<K>, T>
	Entry<K, Set<T>> getMax(Black<K, T> black) {
		if (isNull(black) || black.getEntries().isEmpty()) {
			return null;
		}

		return black.getEntries().firstEntry();
	}

	public static <T, K extends Comparable<K>>
	void getAllAsc(List<Entry<K, Set<T>>> list, FwMersenneBlack<K, T> black) {
		if (isNull(black)) {
			return;
		}

		var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(black.getCapacity());

		if (black.greyRoot == null) {
			for (var grey : black.getGreys()) {
				var res = GreyGetterRange.getAllAsc(grey);
				results.add(res.iterator());
			}
		} else {
			for (var grey = black.greyRoot; grey != null; grey = grey.fwRight) {
				var res = GreyGetterMersenneFw.getAllAsc(grey);
				results.add(res.iterator());
			}
		}

		EntryHandler.mergeAsc(list, results);

		// Now add the entries of the current node in descending order
		for (int i = black.getEntries().size() - 1; i >= 0; i--) {
			list.add(black.getEntries().get(i));
		}
	}

	public static <T, K extends Comparable<K>>
	void getAllDesc(List<Entry<K, Set<T>>> list, FwMersenneBlack<K, T> black) {
		if (isNull(black)) {
			return;
		}

        list.addAll(black.getEntries());

		var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(black.getCapacity());

		if (black.greyRoot == null) {
			for (var grey : black.getGreys()) {
				var res = GreyGetterRange.getAllDesc(grey);
				results.add(res.iterator());
			}
		} else {
			for (var grey = black.greyRoot; grey != null; grey = grey.fwRight) {
				var res = GreyGetterMersenneFw.getAllDesc(grey);
				results.add(res.iterator());
			}
		}

		EntryHandler.mergeDesc(list, results);
	}

	public static <T, K extends Comparable<K>>
	void getBetweenAsc(List<Entry<K, Set<T>>> list, FwMersenneBlack<K, T> black, K from, K to) {
		if (isNull(black)) {
			return;
		}

		var entries = black.getEntries();

		if (from.compareTo(entries.firstEntry().getKey()) > 0) {
			return;
		}

		if (from.compareTo(entries.lastEntry().getKey()) < 0) {
			var results = new ArrayList<Iterator<Entry<K, Set<T>>>>(black.getCapacity());

			if (black.greyRoot == null) {
				for (var grey : black.getGreys()) {
					var res = GreyGetterRange.getBetweenAsc(grey, from, to);
					results.add(res.iterator());
				}
			} else {
				for (var grey = black.greyRoot; grey != null; grey = grey.fwRight) {
					var res = GreyGetterMersenneFw.getBetweenAsc(grey, from, to);
					results.add(res.iterator());
				}
			}

			EntryHandler.mergeAsc(list, results);
		}

		if (to.compareTo(entries.lastEntry().getKey()) >= 0) {
			int index = entries.searchClosest(to);

			for (int i = black.getEntries().size() - 1; i >= index; i--) {
				list.add(black.getEntries().get(i));
			}
		}
	}
}
