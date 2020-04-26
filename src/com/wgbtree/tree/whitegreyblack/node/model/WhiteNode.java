package com.wgbtree.tree.whitegreyblack.node.model;

import com.wgbtree.tree.whitegreyblack.node.util.Mod;

import java.io.Serializable;

public class WhiteNode<K extends Comparable<K>, T> extends WgbNode<K, T> implements Serializable {

    public WhiteNode() {
    }

    public WhiteNode(WgbData<K, T> data, int capacity) {
        super(data, capacity);
    }

    @Override
    public GreyNode<K, T> get(int index) {
        return (GreyNode<K, T>) super.get(index);
    }

    public GreyNode<K, T> next(WgbKey<K> key) {
        return this.get(nextIndex(key));
    }

    @Override
    public int nextIndex(WgbKey<K> key) {
        return Mod.fastMod(Math.abs(key.hashCode()), this.getCapacity());
    }
}