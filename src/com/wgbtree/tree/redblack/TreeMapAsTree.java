package com.wgbtree.tree.redblack;

import com.wgbtree.tree.AsTree;

import java.io.Serializable;
import java.util.*;

public class TreeMapAsTree<K extends Comparable<K>, T> extends TreeMap<K, T> implements AsTree<K, T>, Serializable {

	@Override
	public K getMin() {
		return this.firstKey();
	}

	@Override
	public K getMax() {
		return this.lastKey();
	}

	public int getNumberOfNodes() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public int getNumberOfEmptyNodes() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public int depth() {
		return (int) (Math.log(this.size() + 1) / Math.log(2));
	}

	@Override
	public String getName() {
		return "RB";
	}

	@Override
	public List<Map.Entry<K, Set<T>>> getAllAsc() {
		return super.keySet().stream().map(k -> Map.entry(k, Set.of(super.get(k)))).toList();
	}

	@Override
	public List<Map.Entry<K, Set<T>>> getAllDesc() {
		return super.descendingKeySet().stream().map(k -> Map.entry(k, Set.of(super.get(k)))).toList();
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		return super.keySet().stream().filter(keys::contains).map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getInDesc(List<K> keys) {
		return super.descendingMap().keySet().stream().filter(keys::contains).map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getNotInAsc(List<K> keys) {
		return super.keySet().stream().filter(k -> !keys.contains(k)).map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getNotInDesc(List<K> keys) {
		return super.descendingMap().keySet().stream().filter(k -> !keys.contains(k)).map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Map.Entry<K, Set<T>>> getBetweenAsc(K from, K to) {
		return super.subMap(from, to).entrySet().stream().map(e -> Map.entry(e.getKey(), Set.of(e.getValue()))).toList();
	}

	@Override
	public List<Set<T>> getBetweenDesc(K from, K to) {
		return super.descendingMap().subMap(from, to).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getBetweenAscInclusive(K from, K to) {
		return super.subMap(from, true, to, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getBetweenDescInclusive(K from, K to) {
		return super.descendingMap().subMap(from, true, to, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getNotBetweenAsc(K from, K to) {
		var lessThan = super.headMap(from, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();
		var greaterThan = super.tailMap(to, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();

		return new LinkedList<>() {{
			addAll(lessThan);
			addAll(greaterThan);
		}};
	}

	@Override
	public List<Set<T>> getNotBetweenDesc(K from, K to) {
		var lessThan = super.descendingMap().headMap(from, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();
		var greaterThan = super.descendingMap().tailMap(to, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();

		return new LinkedList<>() {{
			addAll(greaterThan);
			addAll(lessThan);
		}};
	}

	@Override
	public List<Set<T>> getNotBetweenAscInclusive(K from, K to) {
		var lessThan = super.headMap(from, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
		var greaterThan = super.tailMap(to, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();

		return new LinkedList<>() {{
			addAll(lessThan);
			addAll(greaterThan);
		}};
	}

	@Override
	public List<Set<T>> getNotBetweenDescInclusive(K from, K to) {
		var lessThan = super.descendingMap().headMap(from, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
		var greaterThan = super.descendingMap().tailMap(to, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();

		return new LinkedList<>() {{
			addAll(greaterThan);
			addAll(lessThan);
		}};
	}

	@Override
	public List<Set<T>> getGreaterThanAsc(K from) {
		return super.tailMap(from, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getGreaterThanDesc(K from) {
		return super.descendingMap().headMap(from, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getGreaterThanAscInclusive(K from) {
		return super.tailMap(from, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getGreaterThanDescInclusive(K from) {
		return super.descendingMap().headMap(from, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getLessThanAsc(K to) {
		return super.headMap(to, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getLessThanDesc(K to) {
		return super.descendingMap().tailMap(to, false).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getLessThanAscInclusive(K to) {
		return super.headMap(to, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getLessThanDescInclusive(K to) {
		return super.descendingMap().tailMap(to, true).keySet().stream().map(k -> Set.of(super.get(k))).toList();
	}

	@Override
	public List<Set<T>> getNotEqualAsc(K key) {
		return super.entrySet().stream().filter(e -> !e.getKey().equals(key)).map(e -> Set.of(e.getValue())).toList();
	}

	@Override
	public List<Set<T>> getNotEqualDesc(K key) {
		return super.descendingMap().entrySet().stream().filter(e -> !e.getKey().equals(key)).map(e -> Set.of(e.getValue())).toList();
	}
}
