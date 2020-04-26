package com.wgbtree.tree;

import java.util.Map;

public interface AsTree<K extends Comparable<K>, T> extends Map<K, T> {
    int getNumberOfNodes();
    int getNumberOfEmptyNodes();
    int depth();
    String getName();
}
