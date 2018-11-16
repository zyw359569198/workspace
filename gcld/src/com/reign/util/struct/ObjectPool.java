package com.reign.util.struct;

public class ObjectPool<E>
{
    private DoubleLinkedList<E> pool;
    private int increateNum;
    
    public ObjectPool() {
        this.pool = new DoubleLinkedList<E>();
        this.initPool(this.increateNum = 16);
    }
    
    public ObjectPool(final int stepNum) {
        this.pool = new DoubleLinkedList<E>();
        this.initPool(this.increateNum = stepNum);
    }
    
    public Node<E> getObject() {
        if (this.pool.size() > 0) {
            final Node<E> node = this.pool.footer.prev;
            this.pool.remove(node);
            return node;
        }
        this.initPool(this.increateNum);
        return this.getObject();
    }
    
    public void releaseObject(final Node<E> node) {
        this.pool.addBefore(node, this.pool.header.next);
    }
    
    private void initPool(final int num) {
        for (int i = 0; i <= num; ++i) {
            this.pool.addBefore(null);
        }
    }
}
