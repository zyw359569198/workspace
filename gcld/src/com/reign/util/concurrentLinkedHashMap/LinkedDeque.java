package com.reign.util.concurrentLinkedHashMap;

import java.util.*;

final class LinkedDeque<E extends Linked<E>> extends AbstractCollection<E> implements Deque<E>
{
    E first;
    E last;
    
    void linkFirst(final E e) {
        final E f = this.first;
        this.first = e;
        if (f == null) {
            this.last = e;
        }
        else {
            f.setPrevious(e);
            e.setNext(f);
        }
    }
    
    void linkLast(final E e) {
        final E l = this.last;
        this.last = e;
        if (l == null) {
            this.first = e;
        }
        else {
            l.setNext(e);
            e.setPrevious(l);
        }
    }
    
    E unlinkFirst() {
        final E f = this.first;
        final E next = f.getNext();
        f.setNext(null);
        this.first = next;
        if (next == null) {
            this.last = null;
        }
        else {
            next.setPrevious(null);
        }
        return f;
    }
    
    E unlinkLast() {
        final E l = this.last;
        final E prev = l.getPrevious();
        l.setPrevious(null);
        this.last = prev;
        if (prev == null) {
            this.first = null;
        }
        else {
            prev.setNext(null);
        }
        return l;
    }
    
    void unlink(final E e) {
        final E prev = e.getPrevious();
        final E next = e.getNext();
        if (prev == null) {
            this.first = next;
        }
        else {
            prev.setNext(next);
            e.setPrevious(null);
        }
        if (next == null) {
            this.last = prev;
        }
        else {
            next.setPrevious(prev);
            e.setNext(null);
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.first == null;
    }
    
    void checkNotEmpty() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
    }
    
    @Override
    public int size() {
        int size = 0;
        for (E e = this.first; e != null; e = e.getNext()) {
            ++size;
        }
        return size;
    }
    
    @Override
    public void clear() {
        E next;
        for (E e = this.first; e != null; e = next) {
            next = e.getNext();
            e.setPrevious(null);
            e.setNext(null);
        }
        final Linked<E> linked = null;
        this.last = (E)linked;
        this.first = (E)linked;
    }
    
    @Override
    public boolean contains(final Object o) {
        return o instanceof Linked && this.contains((Linked<?>)o);
    }
    
    boolean contains(final Linked<?> e) {
        return e.getPrevious() != null || e.getNext() != null || e == this.first;
    }
    
    public void moveToFront(final E e) {
        if (e != this.first) {
            this.unlink(e);
            this.linkFirst(e);
        }
    }
    
    public void moveToBack(final E e) {
        if (e != this.last) {
            this.unlink(e);
            this.linkLast(e);
        }
    }
    
    @Override
    public E peek() {
        return this.peekFirst();
    }
    
    @Override
    public E peekFirst() {
        return this.first;
    }
    
    @Override
    public E peekLast() {
        return this.last;
    }
    
    @Override
    public E getFirst() {
        this.checkNotEmpty();
        return this.peekFirst();
    }
    
    @Override
    public E getLast() {
        this.checkNotEmpty();
        return this.peekLast();
    }
    
    @Override
    public E element() {
        return this.getFirst();
    }
    
    @Override
    public boolean offer(final E e) {
        return this.offerLast(e);
    }
    
    @Override
    public boolean offerFirst(final E e) {
        if (this.contains(e)) {
            return false;
        }
        this.linkFirst(e);
        return true;
    }
    
    @Override
    public boolean offerLast(final E e) {
        if (this.contains(e)) {
            return false;
        }
        this.linkLast(e);
        return true;
    }
    
    @Override
    public boolean add(final E e) {
        return this.offerLast(e);
    }
    
    @Override
    public void addFirst(final E e) {
        if (!this.offerFirst(e)) {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public void addLast(final E e) {
        if (!this.offerLast(e)) {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public E poll() {
        return this.pollFirst();
    }
    
    @Override
    public E pollFirst() {
        return this.isEmpty() ? null : this.unlinkFirst();
    }
    
    @Override
    public E pollLast() {
        return this.isEmpty() ? null : this.unlinkLast();
    }
    
    @Override
    public E remove() {
        return this.removeFirst();
    }
    
    @Override
    public boolean remove(final Object o) {
        return o instanceof Linked && this.remove((E)o);
    }
    
    boolean remove(final E e) {
        if (this.contains(e)) {
            this.unlink(e);
            return true;
        }
        return false;
    }
    
    @Override
    public E removeFirst() {
        this.checkNotEmpty();
        return this.pollFirst();
    }
    
    @Override
    public boolean removeFirstOccurrence(final Object o) {
        return this.remove(o);
    }
    
    @Override
    public E removeLast() {
        this.checkNotEmpty();
        return this.pollLast();
    }
    
    @Override
    public boolean removeLastOccurrence(final Object o) {
        return this.remove(o);
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean modified = false;
        for (final Object o : c) {
            modified |= this.remove(o);
        }
        return modified;
    }
    
    @Override
    public void push(final E e) {
        this.addFirst(e);
    }
    
    @Override
    public E pop() {
        return this.removeFirst();
    }
    
    @Override
    public Iterator<E> iterator() {
        return new AbstractLinkedIterator(this.first) {
            @Override
            E computeNext() {
                return this.cursor.getNext();
            }
        };
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return new AbstractLinkedIterator(this.last) {
            @Override
            E computeNext() {
                return this.cursor.getPrevious();
            }
        };
    }
    
    abstract class AbstractLinkedIterator implements Iterator<E>
    {
        E cursor;
        
        AbstractLinkedIterator(final E start) {
            this.cursor = start;
        }
        
        @Override
        public boolean hasNext() {
            return this.cursor != null;
        }
        
        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final E e = this.cursor;
            this.cursor = this.computeNext();
            return e;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        abstract E computeNext();
    }
}
