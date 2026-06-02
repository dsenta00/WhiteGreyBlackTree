package com.wgbtree.tree.wgb.model.node.black;

import com.wgbtree.tree.wgb.model.node.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Getter
public class RangeBlack<K extends Comparable<K>, T> extends Black<K, T> implements Serializable {

    public RangeBlack(int order, boolean allowMergingOnSameKey) {
        super(order, 1, allowMergingOnSameKey);
    }

    @Override
    public Node<K, T> nextNode(K key, int keyHash) {
        return (greys == null) ? null : greys[0];
    }
}
