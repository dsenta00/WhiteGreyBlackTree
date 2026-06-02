package com.wgbtree.tree.wgb.model.node.black;

import com.wgbtree.tree.wgb.model.node.grey.FwGrey;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
public class FwMersenneBlack<K extends Comparable<K>, T> extends MersenneBlack<K, T> implements Serializable {
	public FwGrey<K, T> greyRoot;

	public FwMersenneBlack(int order, int power, boolean allowMergingOnSameKey) {
		super(order, power, allowMergingOnSameKey);
		this.greyRoot = null;
	}
}
