package com.wgbtree.tree.wgb.model.node.white;

import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
public class FwMersenneWhite<K extends Comparable<K>, T> extends MersenneWhite<K, T> implements Serializable {
	public FwGrey<K, T> greyRoot;

	public FwMersenneWhite(int order, int power, boolean allowMergingOnSameKey) {
		super(order, power, allowMergingOnSameKey);
		this.greyRoot = null;
	}
}
