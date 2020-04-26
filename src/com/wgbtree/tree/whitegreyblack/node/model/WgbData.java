package com.wgbtree.tree.whitegreyblack.node.model;

import java.io.Serializable;

public class WgbData<K extends Comparable<K>, T> implements Serializable {
    private WgbKey<K> key;
    private T value;

    public WgbData(K key, T value) {
        this.key = new WgbKey<>(key);
        this.value = value;
    }

    public WgbKey<K> getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public void setKey(WgbKey<K> key) {
        this.key = key;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + value.hashCode();
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        var other = (WgbData<K, T>) obj;
        return this.getKey().compareTo(other.getKey()) == 0;
    }
}
