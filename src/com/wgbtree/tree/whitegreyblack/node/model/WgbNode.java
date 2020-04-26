package com.wgbtree.tree.whitegreyblack.node.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class WgbNode<K extends Comparable<K>, T> implements Serializable {
    private int depth;
    private int capacity;
    private WgbData<K, T> data;
    private WgbNode<K, T>[] nodes;

    public WgbNode() {
    }

    public WgbNode(WgbData<K, T> data, int capacity) {
        this.data = data;
        this.depth = 1;
        this.capacity = capacity;
    }

    public abstract int nextIndex(WgbKey<K> key);

    public int getCapacity() {
        return capacity;
    }

    public int getDepth() {
        return depth;
    }

    public WgbData<K, T> getData() {
        return data;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setData(WgbData<K, T> data) {
        this.data = data;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setNodes(WgbNode<K, T>[] nodes) {
        this.nodes = nodes;
    }

    public WgbKey<K> getKey() {
        return data.getKey();
    }

    public WgbNode<K, T> get(int index) {
        return Objects.isNull(nodes) ? null : nodes[index];
    }

    @SuppressWarnings("unchecked")
    public void setNode(int index, WgbNode<K, T> node) {
        nodes = Objects.isNull(nodes) ? new WgbNode[capacity] : nodes;
        nodes[index] = node;
    }

    public void setKey(WgbKey<K> key) {
        this.data.setKey(key);
    }

    public List<WgbNode<K, T>> getNodes() {
        return Objects.isNull(this.nodes) ? List.of() :
                Stream.of(this.nodes)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    public int getNumberOfEmptySlots() {
        return Objects.isNull(this.nodes) ? 0 : (int) Stream.of(this.nodes).filter(Objects::isNull).count();
    }
}