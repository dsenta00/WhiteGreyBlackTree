package com.wgbtree.tree.wgb.model.node.grey;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
public class FwGrey<K extends Comparable<K>, T> extends Grey<K, T> implements Serializable {
	public FwGrey<K, T> fwLeft;
	public FwGrey<K, T> fwRight;

	public FwGrey(int order, boolean allowMergingOnSameKey) {
		super(order, allowMergingOnSameKey);
		fwLeft = null;
		fwRight = null;
	}
}
