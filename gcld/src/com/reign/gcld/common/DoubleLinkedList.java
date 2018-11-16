package com.reign.gcld.common;

public class DoubleLinkedList<E> implements DoubleIterable<Node<E>>
{
    protected Node<E> header;
    protected Node<E> footer;
    private int size;
    private ObjectPool<E> pool;
    
    public DoubleLinkedList() {
        this.header = null;
        this.footer = null;
        this.size = 0;
        this.init();
    }
    
    public DoubleLinkedList(final ObjectPool<E> pool) {
        this.header = null;
        this.footer = null;
        this.size = 0;
        this.pool = pool;
        this.init();
    }
    
    private void init() {
        this.header = this.getNode(null);
        this.footer = this.getNode(null);
        this.header.prev = null;
        this.header.next = this.footer;
        this.footer.prev = this.header;
        this.footer.next = null;
    }
    
    @Override
    public DoubleIterator<Node<E>> iterator(final boolean reverse) {
        return new MyDoubleIterator(this, reverse);
    }
    
    public synchronized void add(final E e) {
        final Node<E> node = this.getNode(e);
        node.prev = this.footer.prev;
        node.next = this.footer;
        this.footer.prev.next = node;
        this.footer.prev = node;
        ++this.size;
    }
    
    public synchronized Node<E> addWithReturn(final E e) {
        final Node<E> node = this.getNode(e);
        node.prev = this.footer.prev;
        node.next = this.footer;
        this.footer.prev.next = node;
        this.footer.prev = node;
        ++this.size;
        return node;
    }
    
    public synchronized void addBefore(final E e) {
        final Node<E> node = this.getNode(e);
        node.prev = this.header;
        node.next = this.header.next;
        this.header.next.prev = node;
        this.header.next = node;
        ++this.size;
    }
    
    public synchronized Node<E> addBeforeWithReturn(final E e) {
        final Node<E> node = this.getNode(e);
        node.prev = this.header;
        node.next = this.header.next;
        this.header.next.prev = node;
        this.header.next = node;
        ++this.size;
        return node;
    }
    
    public synchronized void add(final E e, int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("illegal index :" + index);
        }
        Node<E> snode = null;
        if (index < this.size >> 1) {
            snode = this.header;
            while (index >= 0) {
                snode = snode.next;
                --index;
            }
        }
        else {
            snode = this.footer;
            for (index = this.size - index - 1; index > 0; --index) {
                snode = snode.prev;
            }
        }
        this.addBefore(e, snode);
    }
    
    public synchronized void add(final E e, final Node<E> node) {
        final Node<E> current = this.getNode(e);
        current.prev = node;
        current.next = node.next;
        node.next.prev = current;
        node.next = current;
        ++this.size;
    }
    
    public synchronized Node<E> addWithReturn(final E e, final Node<E> node) {
        final Node<E> current = this.getNode(e);
        current.prev = node;
        current.next = node.next;
        node.next.prev = current;
        node.next = current;
        ++this.size;
        return current;
    }
    
    public synchronized void addBefore(final E e, final Node<E> node) {
        final Node<E> current = this.getNode(e);
        current.next = node;
        current.prev = node.prev;
        node.prev.next = current;
        node.prev = current;
        ++this.size;
    }
    
    public synchronized Node<E> addBeforeWithReturn(final E e, final Node<E> node) {
        final Node<E> current = this.getNode(e);
        current.next = node;
        current.prev = node.prev;
        node.prev.next = current;
        node.prev = current;
        ++this.size;
        return current;
    }
    
    public synchronized void addBefore(final Node<E> current, final Node<E> node) {
        current.next = node;
        current.prev = node.prev;
        node.prev.next = current;
        node.prev = current;
        ++this.size;
    }
    
    public synchronized void remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("illegal index :" + index);
        }
        Node<E> snode = null;
        if (index < this.size >> 1) {
            snode = this.header;
            while (index >= 0) {
                snode = snode.next;
                --index;
            }
        }
        else {
            snode = this.footer;
            for (index = this.size - index; index > 0; --index) {
                snode = snode.prev;
            }
        }
        this.remove(snode);
    }
    
    public synchronized void remove(final Node<E> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        if (this.pool != null) {
            this.pool.releaseObject(node);
        }
        --this.size;
    }
    
    public E get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("illegal index :" + index);
        }
        Node<E> snode = null;
        if (index < this.size >> 1) {
            snode = this.header;
            while (index >= 0) {
                snode = snode.next;
                --index;
            }
            return snode.e;
        }
        snode = this.footer;
        for (index = this.size - index; index > 0; --index) {
            snode = snode.prev;
        }
        return snode.e;
    }
    
    public synchronized E pop() {
        if (this.size <= 0) {
            throw new IndexOutOfBoundsException("illeagal index: -1");
        }
        final Node<E> node = this.footer.prev;
        final E e = node.e;
        this.remove(node);
        return e;
    }
    
    public synchronized E popBefore() {
        if (this.size <= 0) {
            throw new IndexOutOfBoundsException("illeagal index: -1");
        }
        final Node<E> node = this.header.next;
        final E e = node.e;
        this.remove(node);
        return e;
    }
    
    public Node<E> getNode(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("illegal index :" + index);
        }
        Node<E> snode = null;
        if (index < this.size >> 1) {
            snode = this.header;
            while (index >= 0) {
                snode = snode.next;
                --index;
            }
            return snode;
        }
        snode = this.footer;
        for (index = this.size - index; index > 0; --index) {
            snode = snode.prev;
        }
        return snode;
    }
    
    public int size() {
        return this.size;
    }
    
    protected Node<E> getNode(final E e) {
        Node<E> node = null;
        if (this.pool != null) {
            node = this.pool.getObject();
            node.e = e;
        }
        else {
            node = new Node<E>(e);
        }
        return node;
    }
    
    public static void main(final String[] args) {
        final DoubleLinkedList<Integer> list = new DoubleLinkedList<Integer>(new ObjectPool<Integer>(100));
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
    }
    
    private class MyDoubleIterator implements DoubleIterator<Node<E>>
    {
        private Node<E> temp;
        private DoubleLinkedList<E> doubleLinkedList;
        private boolean reverse;
        
        public MyDoubleIterator(final DoubleLinkedList<E> doubleLinkedList, final boolean reverse) {
            this.temp = null;
            this.doubleLinkedList = null;
            if (reverse) {
                this.temp = doubleLinkedList.footer;
            }
            else {
                this.temp = doubleLinkedList.header;
            }
            this.doubleLinkedList = doubleLinkedList;
            this.reverse = reverse;
        }
        
        @Override
        public boolean hasNext() {
            return this.temp != null && this.temp.next != this.doubleLinkedList.footer;
        }
        
        @Override
        public boolean hasPrev() {
            return this.temp != null && this.temp.prev != this.doubleLinkedList.header;
        }
        
        @Override
        public Node<E> next() {
            return this.temp = this.temp.next;
        }
        
        @Override
        public Node<E> prev() {
            return this.temp = this.temp.prev;
        }
        
        @Override
        public void remove() {
            final Node<E> node = this.temp;
            if (this.reverse) {
                if (this.hasPrev()) {
                    this.temp = this.next();
                }
                else {
                    this.temp = null;
                }
            }
            else if (this.hasNext()) {
                this.temp = this.prev();
            }
            else {
                this.temp = null;
            }
            this.doubleLinkedList.remove(node);
        }
    }
}
