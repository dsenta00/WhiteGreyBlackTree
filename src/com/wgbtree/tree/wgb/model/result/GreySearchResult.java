package com.wgbtree.tree.wgb.model.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GreySearchResult<K extends Comparable<K>, T> {
	private Integer index;
	private Map.Entry<K, Set<T>> entry;

	public static <K extends Comparable<K>, T> GreySearchResult<K, T> of(int index, Map.Entry<K, Set<T>> entry) {
		return new GreySearchResult<>(index, entry);
	}

	public static <K extends Comparable<K>, T> GreySearchResult<K, T> empty() {
		return new GreySearchResult<>();
	}

	public boolean isEmpty() {
		return entry == null;
	}

	public boolean isPresent() {
		return entry != null;
	}
}