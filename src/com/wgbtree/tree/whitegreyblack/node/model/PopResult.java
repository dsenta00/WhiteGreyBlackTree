package com.wgbtree.tree.whitegreyblack.node.model;

public class PopResult<K extends Comparable<K>, T, N extends WgbNode<K, T>> {
    private N node;
    private WgbData<K, T> data;

    public PopResult(N node, WgbData<K, T> data) {
        this.node = node;
        this.data = data;
    }

    public WgbData<K, T> getData() {
        return data;
    }

    public N getNode() {
        return node;
    }

    public void setData(WgbData<K, T> data) {
        this.data = data;
    }

    public void setNode(N node) {
        this.node = node;
    }
}
