package com.reign.util.struct.charts.RBTree;

import com.reign.util.struct.charts.*;

public class RedBlackTreeNode<T extends ISetSeqable<T>>
{
    public static final boolean RED = false;
    public static final boolean BLACK = true;
    private T data;
    private RedBlackTreeNode<T> parent;
    private RedBlackTreeNode<T> left;
    private RedBlackTreeNode<T> right;
    private boolean color;
    
    public RedBlackTreeNode(final T data, final RedBlackTreeNode<T> parent, final RedBlackTreeNode<T> left, final RedBlackTreeNode<T> right) {
        this.color = true;
        this.data = data;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toString() {
        return "[data=" + this.data + ", color=" + this.color + "]";
    }
    
    public RedBlackTreeNode<T> getParent() {
        return this.parent;
    }
    
    public void setParent(final RedBlackTreeNode<T> parent) {
        this.parent = parent;
    }
    
    public RedBlackTreeNode<T> getLeft() {
        return this.left;
    }
    
    public void setLeft(final RedBlackTreeNode<T> left) {
        this.left = left;
    }
    
    public RedBlackTreeNode<T> getRight() {
        return this.right;
    }
    
    public void setRight(final RedBlackTreeNode<T> right) {
        this.right = right;
    }
    
    public boolean getColor() {
        return this.color;
    }
    
    public void setColor(final boolean color) {
        this.color = color;
    }
    
    public T getData() {
        return this.data;
    }
    
    public void setData(final T data) {
        this.data = data;
    }
    
    public void clearNode() {
        this.parent = null;
        this.left = null;
        this.right = null;
    }
}
