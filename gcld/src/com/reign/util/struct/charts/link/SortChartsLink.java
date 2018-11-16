package com.reign.util.struct.charts.link;

import java.util.*;
import com.reign.util.struct.charts.*;

public class SortChartsLink<T extends ISetSeqable<T>> implements ISortCharts<T>
{
    private LinkNode<T> root;
    private Map<Integer, LinkNode<T>> map;
    
    public SortChartsLink() {
        this.map = new HashMap<Integer, LinkNode<T>>();
        this.root = null;
    }
    
    @Override
    public void setNeedResetSeqWhenChange(final boolean needResetSeqWhenChange) {
    }
    
    @Override
    public int getSize() {
        return this.map.size();
    }
    
    @Override
    public void add(final T data) {
        if (this.map.containsKey(data.getKey())) {
            return;
        }
        if (this.root != null) {
            final LinkNode<T> newNode = new LinkNode<T>(data, null, null);
            LinkNode<T> currNode = this.root;
            boolean notFindNode = false;
            while (true) {
                while (currNode.getData().compareTo(data) <= 0) {
                    if (currNode.getNext() == null) {
                        notFindNode = true;
                        if (notFindNode) {
                            currNode.setNext(newNode);
                            newNode.setPrev(currNode);
                            newNode.getData().setSeq(currNode.getData().getSeq() + 1);
                        }
                        else {
                            if (currNode.getPrev() != null) {
                                currNode.getPrev().setNext(newNode);
                            }
                            newNode.setPrev(currNode.getPrev());
                            newNode.setNext(currNode);
                            currNode.setPrev(newNode);
                            if (newNode.getPrev() == null) {
                                this.root = newNode;
                                newNode.getData().setSeq(1);
                            }
                            else {
                                newNode.getData().setSeq(newNode.getPrev().getData().getSeq() + 1);
                            }
                            while (currNode != null) {
                                currNode.getData().setSeq(currNode.getPrev().getData().getSeq() + 1);
                                currNode = currNode.getNext();
                            }
                        }
                        this.map.put(data.getKey(), newNode);
                        return;
                    }
                    currNode = currNode.getNext();
                }
                continue;
            }
        }
        this.root = new LinkNode<T>(data, null, null);
        data.setSeq(1);
        this.map.put(data.getKey(), this.root);
    }
    
    @Override
    public T getData(final int key) {
        final LinkNode<T> node = this.map.get(key);
        if (node == null) {
            return null;
        }
        return node.getData().getClone();
    }
    
    @Override
    public boolean contains(final int key) {
        return this.map.containsKey(key);
    }
    
    @Override
    public void remove(final int key) {
        final LinkNode<T> target = this.map.get(key);
        if (target == null) {
            return;
        }
        if (target.getNext() == null) {
            if (target == this.root) {
                this.root = null;
            }
            else {
                target.getPrev().setNext(null);
            }
        }
        else {
            if (target == this.root) {
                this.root = target.getNext();
                target.getNext().setPrev(null);
                this.root.getData().setSeq(1);
            }
            else {
                target.getPrev().setNext(target.getNext());
                target.getNext().setPrev(target.getPrev());
                target.getNext().getData().setSeq(target.getData().getSeq());
            }
            for (LinkNode<T> currNode = target.getNext(); currNode.getNext() != null; currNode = currNode.getNext()) {
                currNode.getNext().getData().setSeq(currNode.getData().getSeq() + 1);
            }
        }
        target.setNext(null);
        target.setPrev(null);
        this.map.remove(key);
    }
    
    @Override
    public void addOrChange(final T data) {
        final LinkNode<T> target = this.map.get(data.getKey());
        if (target == null) {
            this.add(data);
        }
        else {
            this.remove(data.getKey());
            this.add(data);
        }
    }
    
    @Override
    public List<T> inIterator() {
        final List<T> list = new ArrayList<T>();
        for (LinkNode<T> currNode = this.root; currNode != null; currNode = currNode.getNext()) {
            list.add(currNode.getData().getClone());
        }
        return list;
    }
    
    @Override
    public void operateItem(final IChartsItemOperator<T> operator) {
        for (LinkNode<T> currNode = this.root; currNode != null; currNode = currNode.getNext()) {
            operator.operate(currNode.getData());
        }
    }
    
    @Override
    public List<T> inIterator(final int maxNum) {
        final List<T> list = new ArrayList<T>();
        LinkNode<T> currNode = this.root;
        int num = 0;
        while (currNode != null) {
            list.add(currNode.getData().getClone());
            if (++num >= maxNum) {
                break;
            }
            currNode = currNode.getNext();
        }
        return list;
    }
    
    @Override
    public List<T> inIterator(final int minSeq, final int maxSeq) {
        final List<T> list = new ArrayList<T>();
        for (LinkNode<T> currNode = this.root; currNode != null; currNode = currNode.getNext()) {
            if (currNode.getData().getSeq() >= minSeq && currNode.getData().getSeq() <= maxSeq) {
                list.add(currNode.getData().getClone());
            }
            if (currNode.getData().getSeq() >= maxSeq) {
                break;
            }
        }
        return list;
    }
    
    @Override
    public void setSeq() {
        LinkNode<T> currNode = this.root;
        for (int seq = 1; currNode != null; currNode = currNode.getNext(), ++seq) {
            currNode.getData().setSeq(seq);
        }
    }
    
    @Override
    public T getDataAtSeq(final int seq) {
        if (this.getSize() < seq) {
            return null;
        }
        final List<T> temp = this.inIterator(seq);
        if (temp.size() == seq) {
            return temp.get(seq - 1);
        }
        return null;
    }
    
    @Override
    public T getData(final int key, final int offset) {
        throw new RuntimeException("not implement.");
    }
    
    @Override
    public List<T> inIterator(final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn) {
        throw new RuntimeException("not implement.");
    }
    
    @Override
    public T getData(final int key, final boolean needClone) {
        return null;
    }
    
    @Override
    public T getDataAtSeq(final int seq, final boolean needClone) {
        return null;
    }
    
    @Override
    public T getData(final int key, final int offset, final boolean needClone) {
        return null;
    }
    
    @Override
    public List<T> inIterator(final boolean needClone) {
        return null;
    }
    
    @Override
    public List<T> inIterator(final int maxNum, final boolean needClone) {
        return null;
    }
    
    @Override
    public List<T> inIterator(final int minSeq, final int maxSeq, final boolean needClone) {
        return null;
    }
    
    @Override
    public List<T> inIterator(final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn, final boolean needClone) {
        return null;
    }
}
