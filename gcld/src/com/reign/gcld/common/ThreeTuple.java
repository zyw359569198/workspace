package com.reign.gcld.common;

import java.io.*;

public class ThreeTuple<L, M, R> implements Serializable
{
    private static final long serialVersionUID = 3965333165733577499L;
    public L left;
    public M middle;
    public R right;
    
    public ThreeTuple() {
    }
    
    public ThreeTuple(final L left, final M middle, final R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    
    @Override
    public int hashCode() {
        return this.left.hashCode() ^ this.middle.hashCode() ^ this.right.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof ThreeTuple) {
            final ThreeTuple to = (ThreeTuple)other;
            return this.left.equals(to.left) && this.middle.equals(to.middle) && this.right.equals(to.right);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[left=" + this.left + ", middle=" + this.middle + ", right=" + this.right + "]";
    }
}
