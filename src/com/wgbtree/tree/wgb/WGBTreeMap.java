package com.wgbtree.tree.wgb;

import com.wgbtree.tree.AsTree;
import com.wgbtree.tree.wgb.handler.GreyHandler;
import com.wgbtree.tree.wgb.model.info.TreeConfig;
import com.wgbtree.tree.wgb.model.node.grey.Grey;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class WGBTreeMap<K extends Comparable<K>, T> implements AsTree<K, T>, Serializable {

	protected final TreeConfig config;
	protected Grey<K, T> grey;

	protected WGBTreeMap(TreeConfig config) {
		this.config = config;
		grey = null;
	}

	@Override
	public int depth() {
		return GreyHandler.depth(grey);
	}

	@Override
	public String getName() {
		return config.name();
	}

	public abstract WGBTreeMap<K, T> cloneEmpty();

	@Override
	public int size() {
		return GreyHandler.size(grey);
	}

	@Override
	public boolean isEmpty() {
		return GreyHandler.size(grey) == 0;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void putAll(Map<? extends K, ? extends T> m) {
		m.forEach(this::put);
	}

	@Override
	public void clear() {
		grey = null;
	}

	@Override
	public Set<K> keySet() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Collection<T> values() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public Set<Entry<K, T>> entrySet() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
}
