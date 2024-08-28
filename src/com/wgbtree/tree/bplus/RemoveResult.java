package com.wgbtree.tree.bplus;

public class RemoveResult {

    public boolean isRemoved;
    public boolean isUnderflow;

    public RemoveResult(boolean isRemoved, boolean isUnderflow) {
        this.isRemoved = isRemoved;
        this.isUnderflow = isUnderflow;
    }
}
