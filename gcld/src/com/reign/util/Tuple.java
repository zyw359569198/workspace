package com.reign.util;

import java.io.*;

public class Tuple<L, R> implements Serializable
{
    public L left;
    public R right;
    private static final long serialVersionUID = 1L;
    
    public Tuple(final L left, final R right) {
        this.left = left;
        this.right = right;
    }
    
    public Tuple() {
    }
    
    @Override
    public int hashCode() {
        return this.left.hashCode() ^ this.right.hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Tuple) {
            final Tuple to = (Tuple)other;
            return this.left.equals(to.left) && this.right.equals(to.right);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[left=" + this.left + ", right=" + this.right + "]";
    }
}
