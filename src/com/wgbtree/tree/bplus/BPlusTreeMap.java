package com.wgbtree.tree.bplus;

import com.wgbtree.tree.AsTree;

import java.util.*;

public class BPlusTreeMap<K extends Comparable<K>, T> implements AsTree<K, T> {
	private final int OVERFLOW_BOUND;
	private final int UNDERFLOW_BOUND;
	private BPlusTreeNode root;
	private int size;

	public BPlusTreeMap(int order) {
		if (order < 3) {
			throw new IllegalArgumentException("The order of BPlus Tree must be greater than or equal to 3");
		}
		this.OVERFLOW_BOUND = order - 1;
		this.UNDERFLOW_BOUND = OVERFLOW_BOUND / 2;
	}

	public BPlusTreeMap() {
		this.OVERFLOW_BOUND = 8;
		this.UNDERFLOW_BOUND = OVERFLOW_BOUND / 2;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(Object key) {
		List<T> result = query((K) key);
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public T put(K key, T value) {
		T oldValue = get(key);
		if (oldValue != null) {
			update(key, oldValue, value);
		} else {
			insert(key, value);
		}
		return oldValue;
	}

	@Override
	public T remove(Object key) {
		if (remove((K) key)) {
			size--;
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends T> m) {
		for (Map.Entry<? extends K, ? extends T> entry : m.entrySet()) {
			insert(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<T> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<K, T>> entrySet() {
		throw new UnsupportedOperationException();
	}

	public void insert(K key, T value) {
		if (root == null) {
			root = new BPlusTreeLeafNode(asList(key), asList(asSet(value)));
		} else {
			BPlusTreeNode newChildNode = root.insert(key, value);
			if (newChildNode != null) {
				K newRootEntry = newChildNode.entries.get(0);
				root = new BPlusTreeNonLeafNode(asList(newRootEntry), asList(root, newChildNode));
			}
		}
		size++;
	}

	public List<T> query(K entry) {
		if (root == null) {
			return Collections.emptyList();
		}
		return root.query(entry);
	}

	public List<T> rangeQuery(K startInclude, K endExclude) {
		if (startInclude.compareTo(endExclude) >= 0) {
			throw new IllegalArgumentException("invalid range");
		}

		if (root == null) {
			return Collections.emptyList();
		}

		return root.rangeQuery(startInclude, endExclude);
	}

	public void update(K entry, T oldValue, T newValue) {
		if (root == null) {
			return;
		}

		root.update(entry, oldValue, newValue);
	}

	public boolean remove(K entry, T value) {
		if (root == null) {
			return false;
		}

		RemoveResult removeResult = root.remove(entry, value);
		if (!removeResult.isRemoved) {
			return false;
		}

		if (root.entries.isEmpty()) {
			this.handleRootUnderflow();
		}

		return true;
	}

	public boolean remove(K entry) {
		if (root == null) {
			return false;
		}

		RemoveResult removeResult = root.remove(entry);
		if (!removeResult.isRemoved) {
			return false;
		}

		if (root.entries.isEmpty()) {
			this.handleRootUnderflow();
		}

		return true;
	}

	private void handleRootUnderflow() {
		root = root.getClass().equals(BPlusTreeLeafNode.class) ? null : ((BPlusTreeNonLeafNode) root).children.get(0);
	}

	@SafeVarargs
	private final <T> List<T> asList(T... e) {
		return new ArrayList<>(Arrays.asList(e));
	}

	private final <T> Set<T> asSet(T e) {
		var set = new HashSet<T>();
		set.add(e);
		return set;
	}

	@Override
	public String toString() {
		if (root == null) {
			return "";
		}
		return root.toString();
	}

	@Override
	public K getMin() {
		if (root == null) {
			return null;
		}
		return root.getClass().equals(BPlusTreeLeafNode.class) ? root.entries.get(0) : ((BPlusTreeNonLeafNode) root).children.get(0).entries.get(0);
	}

	@Override
	public K getMax() {
		if (root == null) {
			return null;
		}
		return root.getClass().equals(BPlusTreeLeafNode.class) ? root.entries.get(root.entries.size() - 1) : ((BPlusTreeNonLeafNode) root).children.get(((BPlusTreeNonLeafNode) root).children.size() - 1).entries.get(0);
	}

	@Override
	public int depth() {
		int depth = 0;
		BPlusTreeNode cur = root;
		while (cur != null) {
			++depth;
			if (cur.getClass().equals(BPlusTreeLeafNode.class)) {
				break;
			}
			cur = ((BPlusTreeNonLeafNode) cur).children.get(0);
		}
		return depth;
	}

	@Override
	public String getName() {
		return "b+|o:" + (this.OVERFLOW_BOUND + 1);
	}

	@Override
	public List<Entry<K, Set<T>>> getAllAsc() {
		return root == null ? Collections.emptyList() : getAllAsc(root);
	}

	private List<Entry<K, Set<T>>> getAllAsc(BPlusTreeNode node) {
		List<Entry<K, Set<T>>> res = new ArrayList<>();
		if (node instanceof BPlusTreeLeafNode leafNode) {
			leafNode.data.forEach(d -> res.add(Map.entry(node.entries.get(0), d)));
		} else {
			for (var child : ((BPlusTreeNonLeafNode) node).children) {
				res.addAll(getAllAsc(child));
			}
		}
		return res;
	}

	@Override
	public List<Entry<K, Set<T>>> getAllDesc() {
		return root == null ? Collections.emptyList() : getAllDesc(root);
	}

	private List<Entry<K, Set<T>>> getAllDesc(BPlusTreeNode node) {
		List<Entry<K, Set<T>>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			((BPlusTreeLeafNode) node).data.forEach(d -> res.add(Map.entry(node.entries.get(0), d)));
		} else {
			for (int i = ((BPlusTreeNonLeafNode) node).children.size() - 1; i >= 0; --i) {
				res.addAll(getAllDesc(((BPlusTreeNonLeafNode) node).children.get(i)));
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getInAsc(List<K> keys) {
		return root == null ? Collections.emptyList() : getInAsc(root, keys);
	}

	private List<Set<T>> getInAsc(BPlusTreeNode node, List<K> keys) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (K key : keys) {
				int index = ((BPlusTreeLeafNode) node).getEqualEntryIndex(key);
				if (index != -1) {
					res.add(((BPlusTreeLeafNode) node).data.get(index));
				}
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (keys.contains(node.entries.get(i))) {
					res.addAll(getInAsc(((BPlusTreeNonLeafNode) node).children.get(i), keys));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getInDesc(List<K> keys) {
		return root == null ? Collections.emptyList() : getInDesc(root, keys);
	}

	private List<Set<T>> getInDesc(BPlusTreeNode node, List<K> keys) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (K key : keys) {
				int index = ((BPlusTreeLeafNode) node).getEqualEntryIndex(key);
				if (index != -1) {
					res.add(((BPlusTreeLeafNode) node).data.get(index));
				}
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (keys.contains(node.entries.get(i))) {
					res.addAll(getInDesc(((BPlusTreeNonLeafNode) node).children.get(i), keys));
				}
			}
		}

		return res;
	}

	@Override
	public List<Set<T>> getNotInAsc(List<K> keys) {
		return root == null ? Collections.emptyList() : getNotInAsc(root, keys);
	}

	private List<Set<T>> getNotInAsc(BPlusTreeNode node, List<K> keys) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (!keys.contains(node.entries.get(i))) {
					res.add(((BPlusTreeLeafNode) node).data.get(i));
				}
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (!keys.contains(node.entries.get(i))) {
					res.addAll(getNotInAsc(((BPlusTreeNonLeafNode) node).children.get(i), keys));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotInDesc(List<K> keys) {
		return root == null ? Collections.emptyList() : getNotInDesc(root, keys);
	}

	private List<Set<T>> getNotInDesc(BPlusTreeNode node, List<K> keys) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (!keys.contains(node.entries.get(i))) {
					res.add(((BPlusTreeLeafNode) node).data.get(i));
				}
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (!keys.contains(node.entries.get(i))) {
					res.addAll(getNotInDesc(((BPlusTreeNonLeafNode) node).children.get(i), keys));
				}
			}
		}
		return res;
	}

	@Override
	public List<Entry<K, Set<T>>> getBetweenAsc(K from, K to) {
		return root == null ? Collections.emptyList() : getBetweenAsc(root, from, to);
	}

	private List<Entry<K, Set<T>>> getBetweenAsc(BPlusTreeNode node, K from, K to) {
		List<Entry<K, Set<T>>> res = new ArrayList<>();

		if (node instanceof BPlusTreeLeafNode) {
			// Leaf node processing
			BPlusTreeLeafNode leaf = (BPlusTreeLeafNode) node;
			for (int i = 0; i < leaf.entries.size(); ++i) {
				K key = leaf.entries.get(i);
				if (key.compareTo(from) >= 0 && key.compareTo(to) < 0) {
					res.add(Map.entry(key, leaf.data.get(i)));
				}
				if (key.compareTo(to) >= 0) {
					break; // No need to check further entries
				}
			}
		} else {
			// Non-leaf node processing
			BPlusTreeNonLeafNode nonLeaf = (BPlusTreeNonLeafNode) node;
			for (int i = 0; i < nonLeaf.entries.size(); ++i) {
				K key = nonLeaf.entries.get(i);

				if (key.compareTo(from) >= 0) {
					// Recursively process child node
					res.addAll(getBetweenAsc(nonLeaf.children.get(i), from, to));
				}
				if (key.compareTo(to) >= 0) {
					break; // No need to check further entries
				}
			}
			// Process the last child node (if `to` is greater than the last entry key)
			if (nonLeaf.children.size() > nonLeaf.entries.size()) {
				res.addAll(getBetweenAsc(nonLeaf.children.get(nonLeaf.children.size() - 1), from, to));
			}
		}

		return res;
	}

	@Override
	public List<Set<T>> getBetweenDesc(K from, K to) {
		return root == null ? Collections.emptyList() : getBetweenDesc(root, from, to);
	}

	private List<Set<T>> getBetweenDesc(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(to) - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(from) < 0) {
					break;
				}
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(to) < 0) {
					res.addAll(getBetweenDesc(((BPlusTreeNonLeafNode) node).children.get(i + 1), from, to));
				}
				if (node.entries.get(i).compareTo(from) < 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getBetweenAscInclusive(K from, K to) {
		return root == null ? Collections.emptyList() : getBetweenAscInclusive(root, from, to);
	}

	private List<Set<T>> getBetweenAscInclusive(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(from); i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(to) > 0) {
					break;
				}
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) >= 0) {
					res.addAll(getBetweenAscInclusive(((BPlusTreeNonLeafNode) node).children.get(i), from, to));
				}
				if (node.entries.get(i).compareTo(to) > 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getBetweenDescInclusive(K from, K to) {
		return root == null ? Collections.emptyList() : getBetweenDescInclusive(root, from, to);
	}

	private List<Set<T>> getBetweenDescInclusive(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(to) - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(from) < 0) {
					break;
				}
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(to) < 0) {
					res.addAll(getBetweenDescInclusive(((BPlusTreeNonLeafNode) node).children.get(i + 1), from, to));
				}
				if (node.entries.get(i).compareTo(from) < 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotBetweenAsc(K from, K to) {
		return root == null ? Collections.emptyList() : getNotBetweenAsc(root, from, to);
	}

	private List<Set<T>> getNotBetweenAsc(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) < 0 || node.entries.get(i).compareTo(to) >= 0) {
					res.add(((BPlusTreeLeafNode) node).data.get(i));
				}
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) < 0) {
					res.addAll(getNotBetweenAsc(((BPlusTreeNonLeafNode) node).children.get(i), from, to));
				}
				if (node.entries.get(i).compareTo(to) >= 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotBetweenDesc(K from, K to) {
		return root == null ? Collections.emptyList() : getNotBetweenDesc(root, from, to);
	}

	private List<Set<T>> getNotBetweenDesc(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(from) < 0 || node.entries.get(i).compareTo(to) >= 0) {
					res.add(((BPlusTreeLeafNode) node).data.get(i));
				}
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(to) >= 0) {
					res.addAll(getNotBetweenDesc(((BPlusTreeNonLeafNode) node).children.get(i + 1), from, to));
				}
				if (node.entries.get(i).compareTo(from) < 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotBetweenAscInclusive(K from, K to) {
		return root == null ? Collections.emptyList() : getNotBetweenAscInclusive(root, from, to);
	}

	private List<Set<T>> getNotBetweenAscInclusive(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) <= 0 || node.entries.get(i).compareTo(to) > 0) {
					res.add(((BPlusTreeLeafNode) node).data.get(i));
				}
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) <= 0) {
					res.addAll(getNotBetweenAscInclusive(((BPlusTreeNonLeafNode) node).children.get(i), from, to));
				}
				if (node.entries.get(i).compareTo(to) > 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotBetweenDescInclusive(K from, K to) {
		return root == null ? Collections.emptyList() : getNotBetweenDescInclusive(root, from, to);
	}

	private List<Set<T>> getNotBetweenDescInclusive(BPlusTreeNode node, K from, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(from) <= 0 || node.entries.get(i).compareTo(to) > 0) {
					res.add(((BPlusTreeLeafNode) node).data.get(i));
				}
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(to) > 0) {
					res.addAll(getNotBetweenDescInclusive(((BPlusTreeNonLeafNode) node).children.get(i + 1), from, to));
				}
				if (node.entries.get(i).compareTo(from) <= 0) {
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getGreaterThanAsc(K from) {
		return root == null ? Collections.emptyList() : getGreaterThanAsc(root, from);
	}

	private List<Set<T>> getGreaterThanAsc(BPlusTreeNode node, K from) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(from); i < node.entries.size(); ++i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) >= 0) {
					res.addAll(getGreaterThanAsc(((BPlusTreeNonLeafNode) node).children.get(i), from));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getGreaterThanDesc(K from) {
		return root == null ? Collections.emptyList() : getGreaterThanDesc(root, from);
	}

	private List<Set<T>> getGreaterThanDesc(BPlusTreeNode node, K from) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(from) - 1; i >= 0; --i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(from) >= 0) {
					res.addAll(getGreaterThanDesc(((BPlusTreeNonLeafNode) node).children.get(i + 1), from));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getGreaterThanAscInclusive(K from) {
		return root == null ? Collections.emptyList() : getGreaterThanAscInclusive(root, from);
	}

	private List<Set<T>> getGreaterThanAscInclusive(BPlusTreeNode node, K from) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(from); i < node.entries.size(); ++i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(from) > 0) {
					res.addAll(getGreaterThanAscInclusive(((BPlusTreeNonLeafNode) node).children.get(i), from));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getGreaterThanDescInclusive(K from) {
		return root == null ? Collections.emptyList() : getGreaterThanDescInclusive(root, from);
	}

	private List<Set<T>> getGreaterThanDescInclusive(BPlusTreeNode node, K from) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entryIndexUpperBound(from) - 1; i >= 0; --i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(from) > 0) {
					res.addAll(getGreaterThanDescInclusive(((BPlusTreeNonLeafNode) node).children.get(i + 1), from));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getLessThanAsc(K to) {
		return root == null ? Collections.emptyList() : getLessThanAsc(root, to);
	}

	private List<Set<T>> getLessThanAsc(BPlusTreeNode node, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = 0; i < node.entryIndexUpperBound(to); ++i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(to) >= 0) {
					break;
				}
				res.addAll(getLessThanAsc(((BPlusTreeNonLeafNode) node).children.get(i), to));
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getLessThanDesc(K to) {
		return root == null ? Collections.emptyList() : getLessThanDesc(root, to);
	}

	private List<Set<T>> getLessThanDesc(BPlusTreeNode node, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entries.size() - 1; i >= node.entryIndexUpperBound(to); --i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(to) >= 0) {
					res.addAll(getLessThanDesc(((BPlusTreeNonLeafNode) node).children.get(i + 1), to));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getLessThanAscInclusive(K to) {
		return root == null ? Collections.emptyList() : getLessThanAscInclusive(root, to);
	}

	private List<Set<T>> getLessThanAscInclusive(BPlusTreeNode node, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = 0; i < node.entryIndexUpperBound(to) + 1; ++i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(to) > 0) {
					break;
				}
				res.addAll(getLessThanAscInclusive(((BPlusTreeNonLeafNode) node).children.get(i), to));
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getLessThanDescInclusive(K to) {
		return root == null ? Collections.emptyList() : getLessThanDescInclusive(root, to);
	}

	private List<Set<T>> getLessThanDescInclusive(BPlusTreeNode node, K to) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			for (int i = node.entries.size() - 1; i >= node.entryIndexUpperBound(to) - 1; --i) {
				res.add(((BPlusTreeLeafNode) node).data.get(i));
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(to) > 0) {
					res.addAll(getLessThanDescInclusive(((BPlusTreeNonLeafNode) node).children.get(i + 1), to));
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotEqualAsc(K key) {
		return root == null ? Collections.emptyList() : getNotEqualAsc(root, key);
	}

	private List<Set<T>> getNotEqualAsc(BPlusTreeNode node, K key) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			int index = ((BPlusTreeLeafNode) node).getEqualEntryIndex(key);
			if (index == -1) {
				res.addAll(((BPlusTreeLeafNode) node).data);
			}
		} else {
			for (int i = 0; i < node.entries.size(); ++i) {
				if (node.entries.get(i).compareTo(key) >= 0) {
					res.addAll(getNotEqualAsc(((BPlusTreeNonLeafNode) node).children.get(i), key));
					break;
				}
			}
		}
		return res;
	}

	@Override
	public List<Set<T>> getNotEqualDesc(K key) {
		return root == null ? Collections.emptyList() : getNotEqualDesc(root, key);
	}

	private List<Set<T>> getNotEqualDesc(BPlusTreeNode node, K key) {
		List<Set<T>> res = new ArrayList<>();
		if (node.getClass().equals(BPlusTreeLeafNode.class)) {
			int index = ((BPlusTreeLeafNode) node).getEqualEntryIndex(key);
			if (index == -1) {
				res.addAll(((BPlusTreeLeafNode) node).data);
			}
		} else {
			for (int i = node.entries.size() - 1; i >= 0; --i) {
				if (node.entries.get(i).compareTo(key) >= 0) {
					res.addAll(getNotEqualDesc(((BPlusTreeNonLeafNode) node).children.get(i + 1), key));
					break;
				}
			}
		}
		return res;
	}

	private abstract class BPlusTreeNode {

		protected List<K> entries;

		protected boolean isUnderflow() {
			return entries.size() < UNDERFLOW_BOUND;
		}

		protected boolean isOverflow() {
			return entries.size() > OVERFLOW_BOUND;
		}

		protected int getMedianIndex() {
			return OVERFLOW_BOUND / 2;
		}

		protected int entryIndexUpperBound(K entry) {
			int low = 0;
			int high = entries.size();
			while (low < high) {
				int mid = (low + high) >>> 1;
				if (entries.get(mid).compareTo(entry) <= 0) {
					low = mid + 1;
				} else {
					high = mid;
				}
			}
			return low;
		}

		public abstract List<T> rangeQuery(K startInclude, K endExclude);

		public abstract List<T> query(K entry);

		public abstract BPlusTreeNode insert(K entry, T value);

		public abstract boolean update(K entry, T oldValue, T newValue);

		public abstract RemoveResult remove(K entry);

		public abstract RemoveResult remove(K entry, T value);

		public abstract void combine(BPlusTreeNode neighbor, K parentEntry);

		public abstract void borrow(BPlusTreeNode neighbor, K parentEntry, boolean isLeft);
	}

	private class BPlusTreeNonLeafNode extends BPlusTreeNode {

		public List<BPlusTreeNode> children;

		public BPlusTreeNonLeafNode(List<K> entries, List<BPlusTreeNode> children) {
			this.entries = entries;
			this.children = children;
		}

		@Override
		public List<T> rangeQuery(K startInclude, K endExclude) {
			return children.get(entryIndexUpperBound(startInclude)).rangeQuery(startInclude, endExclude);
		}

		@Override
		public List<T> query(K entry) {
			return children.get(entryIndexUpperBound(entry)).query(entry);
		}

		@Override
		public boolean update(K entry, T oldValue, T newValue) {
			return children.get(entryIndexUpperBound(entry)).update(entry, oldValue, newValue);
		}

		@Override
		public BPlusTreeNode insert(K entry, T value) {
			BPlusTreeNode newChildNode = children.get(entryIndexUpperBound(entry)).insert(entry, value);

			if (newChildNode != null) {
				K newEntry = findLeafEntry(newChildNode);
				int newEntryIndex = entryIndexUpperBound(newEntry);
				entries.add(newEntryIndex, newEntry);
				children.add(newEntryIndex + 1, newChildNode);
				return isOverflow() ? split() : null;
			}

			return null;
		}

		@Override
		public RemoveResult remove(K entry) {
			int childIndex = entryIndexUpperBound(entry);
			int entryIndex = Math.max(0, childIndex - 1);
			BPlusTreeNode childNode = children.get(childIndex);
			RemoveResult removeResult = childNode.remove(entry);
			if (!removeResult.isRemoved) {
				return removeResult;
			}

			if (removeResult.isUnderflow) {
				this.handleUnderflow(childNode, childIndex, entryIndex);
			}

			return new RemoveResult(true, isUnderflow());
		}

		@Override
		public RemoveResult remove(K entry, T value) {
			int childIndex = entryIndexUpperBound(entry);
			int entryIndex = Math.max(0, childIndex - 1);

			BPlusTreeNode childNode = children.get(childIndex);
			RemoveResult removeResult = childNode.remove(entry, value);
			if (!removeResult.isRemoved) {
				return removeResult;
			}

			if (removeResult.isUnderflow) {
				this.handleUnderflow(childNode, childIndex, entryIndex);
			}

			return new RemoveResult(true, isUnderflow());
		}


		private void handleUnderflow(BPlusTreeNode childNode, int childIndex, int entryIndex) {
			BPlusTreeNode neighbor;
			if (childIndex > 0 && (neighbor = this.children.get(childIndex - 1)).entries.size() > UNDERFLOW_BOUND) {

				childNode.borrow(neighbor, this.entries.get(entryIndex), true);
				K boundEntry = childNode.getClass().equals(BPlusTreeNonLeafNode.class) ? findLeafEntry(childNode) : childNode.entries.get(0);
				this.entries.set(entryIndex, boundEntry);

			} else if (childIndex < this.children.size() - 1 && (neighbor = this.children.get(childIndex + 1)).entries.size() > UNDERFLOW_BOUND) {

				int parentEntryIndex = childIndex == 0 ? 0 : Math.min(this.entries.size() - 1, entryIndex + 1);
				childNode.borrow(neighbor, this.entries.get(parentEntryIndex), false);
				this.entries.set(parentEntryIndex, childNode.getClass().equals(BPlusTreeNonLeafNode.class) ? findLeafEntry(neighbor) : neighbor.entries.get(0));

			} else {

				if (childIndex > 0) {
					// combine current child to left child
					neighbor = this.children.get(childIndex - 1);
					neighbor.combine(childNode, this.entries.get(entryIndex));
					this.entries.remove(entryIndex);
					this.children.remove(childIndex);

				} else {
					// combine right child to current child (child index = 0)
					neighbor = this.children.get(1);
					childNode.combine(neighbor, this.entries.get(0));
					this.entries.remove(0);
					this.children.remove(1);
				}

			}

		}

		private BPlusTreeNonLeafNode split() {
			int medianIndex = getMedianIndex();
			List<K> allEntries = entries;
			List<BPlusTreeNode> allChildren = children;

			this.entries = new ArrayList<>(allEntries.subList(0, medianIndex));
			this.children = new ArrayList<>(allChildren.subList(0, medianIndex + 1));

			List<K> rightEntries = new ArrayList<>(allEntries.subList(medianIndex + 1, allEntries.size()));
			List<BPlusTreeNode> rightChildren = new ArrayList<>(allChildren.subList(medianIndex + 1, allChildren.size()));
			return new BPlusTreeNonLeafNode(rightEntries, rightChildren);
		}

		@Override
		public void combine(BPlusTreeNode neighbor, K parentEntry) {
			BPlusTreeNonLeafNode nonLeafNode = (BPlusTreeNonLeafNode) neighbor;
			this.entries.add(parentEntry);
			this.entries.addAll(nonLeafNode.entries);
			this.children.addAll(nonLeafNode.children);
		}

		@Override
		public void borrow(BPlusTreeNode neighbor, K parentEntry, boolean isLeft) {
			BPlusTreeNonLeafNode nonLeafNode = (BPlusTreeNonLeafNode) neighbor;
			if (isLeft) {
				this.entries.add(0, parentEntry);
				this.children.add(0, nonLeafNode.children.get(nonLeafNode.children.size() - 1));
				nonLeafNode.children.remove(nonLeafNode.children.size() - 1);
				nonLeafNode.entries.remove(nonLeafNode.entries.size() - 1);
			} else {
				this.entries.add(parentEntry);
				this.children.add(nonLeafNode.children.get(0));
				nonLeafNode.entries.remove(0);
				nonLeafNode.children.remove(0);
			}
		}

		public K findLeafEntry(BPlusTreeNode cur) {
			if (cur.getClass().equals(BPlusTreeLeafNode.class)) {
				return cur.entries.get(0);
			}
			return findLeafEntry(((BPlusTreeNonLeafNode) cur).children.get(0));
		}

		@Override
		public String toString() {
			StringBuilder res = new StringBuilder();
			Queue<BPlusTreeNode> queue = new LinkedList<>();
			queue.add(this);
			while (!queue.isEmpty()) {
				int size = queue.size();
				for (int i = 0; i < size; ++i) {
					BPlusTreeNode cur = queue.poll();
					assert cur != null;
					res.append(cur.entries).append("  ");
					if (cur.getClass().equals(BPlusTreeNonLeafNode.class)) {
						queue.addAll(((BPlusTreeNonLeafNode) cur).children);
					}
				}
				res.append('\n');
			}
			return res.toString();
		}
	}

	private class BPlusTreeLeafNode extends BPlusTreeNode {

		public List<Set<T>> data;

		public BPlusTreeLeafNode next;

		public BPlusTreeLeafNode(List<K> entries, List<Set<T>> data) {
			this.entries = entries;
			this.data = data;
		}

		@Override
		public List<T> rangeQuery(K startInclude, K endExclude) {
			List<T> res = new ArrayList<>();
			int startUpperBound = Math.max(1, entryIndexUpperBound(startInclude));

			int end = entryIndexUpperBound(endExclude) - 1;
			if (end >= 0 && entries.get(end) == endExclude) {
				--end;
			}

			for (int i = startUpperBound - 1; i <= end; ++i) {
				res.addAll(data.get(i));
			}

			BPlusTreeLeafNode nextLeafNode = next;
			while (nextLeafNode != null) {
				for (int i = 0; i < nextLeafNode.entries.size(); ++i) {
					if (nextLeafNode.entries.get(i).compareTo(endExclude) < 0) {
						res.addAll(nextLeafNode.data.get(i));
					} else {
						return res;
					}
				}
				nextLeafNode = nextLeafNode.next;
			}
			return res;
		}

		@Override
		public List<T> query(K entry) {
			int entryIndex = getEqualEntryIndex(entry);
			return entryIndex == -1 ? Collections.emptyList() : new ArrayList<>(data.get(entryIndex));
		}

		@Override
		public boolean update(K entry, T oldValue, T newValue) {
			int entryIndex = getEqualEntryIndex(entry);
			if (entryIndex == -1 || !data.get(entryIndex).contains(oldValue)) {
				return false;
			}
			var set = data.get(entryIndex);
			set.remove(oldValue);
			set.add(newValue);

			return true;
		}

		@Override
		public RemoveResult remove(K entry) {
			int entryIndex = getEqualEntryIndex(entry);
			if (entryIndex == -1) {
				return new RemoveResult(false, false);
			}

			this.entries.remove(entryIndex);
			this.data.remove(entryIndex);

			return new RemoveResult(true, isUnderflow());
		}

		@Override
		public RemoveResult remove(K entry, T value) {
			int entryIndex = getEqualEntryIndex(entry);
			if (entryIndex == -1 || !data.get(entryIndex).contains(value)) {
				return new RemoveResult(false, false);
			}

			data.get(entryIndex).remove(value);
			if (data.get(entryIndex).isEmpty()) {
				this.entries.remove(entryIndex);
				this.data.remove(entryIndex);
			}

			return new RemoveResult(true, isUnderflow());
		}

		@Override
		public void combine(BPlusTreeNode neighbor, K parentEntry) {
			BPlusTreeLeafNode leafNode = (BPlusTreeLeafNode) neighbor;
			this.entries.addAll(leafNode.entries);
			this.data.addAll(leafNode.data);
			this.next = leafNode.next;
		}

		@Override
		public void borrow(BPlusTreeNode neighbor, K parentEntry, boolean isLeft) {
			BPlusTreeLeafNode leafNode = (BPlusTreeLeafNode) neighbor;
			int borrowIndex;

			if (isLeft) {
				borrowIndex = leafNode.entries.size() - 1;
				this.entries.add(0, leafNode.entries.get(borrowIndex));
				this.data.add(0, leafNode.data.get(borrowIndex));
			} else {
				borrowIndex = 0;
				this.entries.add(leafNode.entries.get(borrowIndex));
				this.data.add(leafNode.data.get(borrowIndex));
			}

			leafNode.entries.remove(borrowIndex);
			leafNode.data.remove(borrowIndex);
		}

		@Override
		public BPlusTreeNode insert(K entry, T value) {
			int equalEntryIndex = getEqualEntryIndex(entry);
			if (equalEntryIndex != -1) {
				data.get(equalEntryIndex).add(value);
				return null;
			}

			int index = entryIndexUpperBound(entry);
			entries.add(index, entry);
			data.add(index, asSet(value));
			return isOverflow() ? split() : null;
		}

		private BPlusTreeLeafNode split() {
			int medianIndex = getMedianIndex();
			List<K> allEntries = entries;
			List<Set<T>> allData = data;

			this.entries = new ArrayList<>(allEntries.subList(0, medianIndex));
			this.data = new ArrayList<>(allData.subList(0, medianIndex));

			List<K> rightEntries = new ArrayList<>(allEntries.subList(medianIndex, allEntries.size()));
			List<Set<T>> rightData = new ArrayList<>(allData.subList(medianIndex, allData.size()));
			BPlusTreeLeafNode newLeafNode = new BPlusTreeLeafNode(rightEntries, rightData);

			newLeafNode.next = this.next;
			this.next = newLeafNode;
			return newLeafNode;
		}

		private int getEqualEntryIndex(K entry) {
			int l = 0;
			int r = entries.size() - 1;
			while (l <= r) {
				int mid = l + ((r - l) >> 1);
				int compare = entries.get(mid).compareTo(entry);
				if (compare == 0) {
					return mid;
				} else if (compare > 0) {
					r = mid - 1;
				} else {
					l = mid + 1;
				}
			}
			return -1;
		}

		@Override
		public String toString() {
			return entries.toString();
		}
	}
}