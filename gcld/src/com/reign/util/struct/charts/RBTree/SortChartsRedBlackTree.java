package com.reign.util.struct.charts.RBTree;

import java.util.*;
import com.reign.util.struct.charts.*;

public class SortChartsRedBlackTree<T extends ISetSeqable<T>> implements ISortCharts<T>
{
    private RedBlackTreeNode<T> root;
    private Map<Integer, RedBlackTreeNode<T>> map;
    private boolean needResetSeqWhenChange;
    private int size;
    
    public SortChartsRedBlackTree() {
        this.map = new HashMap<Integer, RedBlackTreeNode<T>>();
        this.root = null;
        this.needResetSeqWhenChange = false;
        this.size = 0;
    }
    
    public SortChartsRedBlackTree(final boolean needResetSeqWhenChange) {
        this.map = new HashMap<Integer, RedBlackTreeNode<T>>();
        this.root = null;
        this.needResetSeqWhenChange = needResetSeqWhenChange;
        this.size = 0;
    }
    
    public SortChartsRedBlackTree(final T data) {
        this.map = new HashMap<Integer, RedBlackTreeNode<T>>();
        this.root = new RedBlackTreeNode<T>(data, null, null, null);
        this.map.put(data.getKey(), this.root);
        this.needResetSeqWhenChange = false;
        this.size = 1;
    }
    
    public SortChartsRedBlackTree(final T data, final boolean needResetSeqWhenChange) {
        this.map = new HashMap<Integer, RedBlackTreeNode<T>>();
        this.root = new RedBlackTreeNode<T>(data, null, null, null);
        this.map.put(data.getKey(), this.root);
        this.needResetSeqWhenChange = needResetSeqWhenChange;
        this.size = 1;
    }
    
    @Override
    public void setNeedResetSeqWhenChange(final boolean needResetSeqWhenChange) {
        this.needResetSeqWhenChange = needResetSeqWhenChange;
    }
    
    @Override
    public void add(final T data) {
        this.add(data, -1, -1);
    }
    
    private void add(final T data, final int sourceSeq, final int nextKey) {
        if (this.map.containsKey(data.getKey())) {
            return;
        }
        if (this.root == null) {
            this.root = new RedBlackTreeNode<T>(data, null, null, null);
            data.setSeq(1);
            this.handleAfterSetSeq(1, data);
            this.map.put(data.getKey(), this.root);
        }
        else {
            RedBlackTreeNode<T> current = this.root;
            RedBlackTreeNode<T> parent = null;
            int cmp = 0;
            do {
                parent = current;
                cmp = data.compareTo(current.getData());
                if (cmp > 0) {
                    current = current.getRight();
                }
                else {
                    current = current.getLeft();
                }
            } while (current != null);
            final RedBlackTreeNode<T> newNode = new RedBlackTreeNode<T>(data, parent, null, null);
            if (cmp > 0) {
                parent.setRight(newNode);
            }
            else {
                parent.setLeft(newNode);
            }
            this.fixAfterInsertion(newNode);
            if (this.needResetSeqWhenChange) {
                if (sourceSeq == -1 || nextKey == -1) {
                    final RedBlackTreeNode<T> newNodePrev = this.getPrevNode(newNode);
                    if (newNodePrev != null) {
                        final ChartsItemOperatorSetSeq<T> chartsItemOperatorSetSeq = new ChartsItemOperatorSetSeq<T>(newNodePrev.getData().getSeq() + 1);
                        this.operateFromNode(newNode, chartsItemOperatorSetSeq);
                    }
                    else {
                        final ChartsItemOperatorSetSeq<T> chartsItemOperatorSetSeq = new ChartsItemOperatorSetSeq<T>(1);
                        this.operateFromNode(newNode, chartsItemOperatorSetSeq);
                    }
                }
                else {
                    final RedBlackTreeNode<T> newNodePrev = this.getPrevNode(newNode);
                    if (newNodePrev == null) {
                        final ChartsItemOperatorSetSeq<T> chartsItemOperatorSetSeq = new ChartsItemOperatorSetSeq<T>(1);
                        this.operateFromNode(newNode, chartsItemOperatorSetSeq);
                    }
                    else if (sourceSeq >= newNodePrev.getData().getSeq()) {
                        final ChartsItemOperatorSetSeq<T> chartsItemOperatorSetSeq = new ChartsItemOperatorSetSeq<T>(newNodePrev.getData().getSeq() + 1);
                        this.operateFromNode(newNode, chartsItemOperatorSetSeq);
                    }
                    else {
                        final RedBlackTreeNode<T> next = this.getNode(nextKey);
                        final ChartsItemOperatorSetSeq<T> chartsItemOperatorSetSeq2 = new ChartsItemOperatorSetSeq<T>(sourceSeq);
                        this.operateFromNode(next, chartsItemOperatorSetSeq2);
                    }
                }
            }
            this.map.put(data.getKey(), newNode);
        }
        ++this.size;
    }
    
    public RedBlackTreeNode<T> getNode(final int key) {
        return this.map.get(key);
    }
    
    @Override
    public T getData(final int key) {
        return this.getData(key, true);
    }
    
    @Override
    public T getData(final int key, final boolean needClone) {
        final RedBlackTreeNode<T> node = this.map.get(key);
        if (node == null) {
            return null;
        }
        if (needClone) {
            return node.getData().getClone();
        }
        return node.getData();
    }
    
    @Override
    public boolean contains(final int key) {
        return this.map.containsKey(key);
    }
    
    @Override
    public int getSize() {
        return this.size;
    }
    
    @Override
    public void remove(final int key) {
        final RedBlackTreeNode<T> target = this.getNode(key);
        if (target == null) {
            return;
        }
        final int sourceSeq = target.getData().getSeq();
        int nextKey = -1;
        RedBlackTreeNode<T> next = this.getNextNode(target);
        if (next != null) {
            nextKey = next.getData().getKey();
        }
        this.remove(target);
        if (this.needResetSeqWhenChange) {
            this.setSeq();
            if (nextKey != -1) {
                next = this.getNode(nextKey);
                final ChartsItemOperatorSetSeq<T> chartsItemOperatorSetSeq = new ChartsItemOperatorSetSeq<T>(sourceSeq);
                this.operateFromNode(next, chartsItemOperatorSetSeq);
            }
        }
        this.map.remove(key);
        --this.size;
    }
    
    @Override
    public void addOrChange(final T data) {
        int sourceSeq = -1;
        int nextKey = -1;
        if (this.contains(data.getKey())) {
            final RedBlackTreeNode<T> target = this.getNode(data.getKey());
            sourceSeq = target.getData().getSeq();
            final RedBlackTreeNode<T> next = this.getNextNode(target);
            if (next != null) {
                nextKey = next.getData().getKey();
            }
            this.remove(target);
            this.map.remove(data.getKey());
            --this.size;
        }
        this.add(data, sourceSeq, nextKey);
    }
    
    public List<T> breadthFirst() {
        final List<T> list = new ArrayList<T>();
        final Queue<RedBlackTreeNode<T>> queue = new ArrayDeque<RedBlackTreeNode<T>>();
        if (this.root != null) {
            queue.offer(this.root);
        }
        while (!queue.isEmpty()) {
            list.add(queue.peek().getData());
            final RedBlackTreeNode<T> p = queue.poll();
            if (p.getLeft() != null) {
                queue.offer(p.getLeft());
            }
            if (p.getRight() != null) {
                queue.offer(p.getRight());
            }
        }
        return list;
    }
    
    @Override
    public List<T> inIterator() {
        return this.inIterator(true);
    }
    
    @Override
    public List<T> inIterator(final boolean needClone) {
        if (this.root == null) {
            return new ArrayList<T>();
        }
        final List<T> list = new ArrayList<T>();
        this.inIterator(this.root, list, needClone);
        return list;
    }
    
    @Override
    public void operateItem(final IChartsItemOperator<T> operator) {
        if (this.root == null) {
            return;
        }
        this.operateItem(this.root, operator);
    }
    
    @Override
    public List<T> inIterator(final int maxNum) {
        return this.inIterator(maxNum, true);
    }
    
    @Override
    public List<T> inIterator(final int maxNum, final boolean needClone) {
        if (this.root == null) {
            return new ArrayList<T>();
        }
        final List<T> list = new ArrayList<T>();
        this.inIterator(this.root, list, maxNum, needClone);
        return list;
    }
    
    @Override
    public List<T> inIterator(final int minSeq, final int maxSeq) {
        return this.inIterator(minSeq, maxSeq, true);
    }
    
    @Override
    public List<T> inIterator(final int minSeq, final int maxSeq, final boolean needClone) {
        if (this.root == null) {
            return new ArrayList<T>();
        }
        final List<T> list = new ArrayList<T>();
        this.inIterator(this.root, list, minSeq, maxSeq, needClone);
        return list;
    }
    
    public List<T> inIteratorFromKey(final int key) {
        if (this.root == null) {
            return new ArrayList<T>();
        }
        final ChartsItemOperatorAddToList<T> operator = new ChartsItemOperatorAddToList<T>();
        final RedBlackTreeNode<T> node = this.map.get(key);
        this.operateFromNode(node, operator);
        return operator.getList();
    }
    
    public List<T> inIteratorFromKey(final int key, final int length) {
        if (this.root == null) {
            return new ArrayList<T>();
        }
        final ChartsItemOperatorAddToList<T> operator = new ChartsItemOperatorAddToList<T>();
        final RedBlackTreeNode<T> node = this.map.get(key);
        this.operateFromNode(node, operator, length);
        return operator.getList();
    }
    
    @Override
    public void setSeq() {
        if (this.root == null) {
            return;
        }
        int seq = 1;
        for (final T t : this.inIterator()) {
            t.setSeq(seq);
            this.handleAfterSetSeq(seq, t);
            ++seq;
        }
    }
    
    @Override
    public T getDataAtSeq(final int seq) {
        return this.getDataAtSeq(seq, true);
    }
    
    @Override
    public T getDataAtSeq(final int seq, final boolean needClone) {
        if (this.size < seq) {
            return null;
        }
        final List<T> temp = this.inIterator(seq, needClone);
        if (temp.size() == seq) {
            return temp.get(seq - 1);
        }
        return null;
    }
    
    public void handleAfterSetSeq(final int seq, final T data) {
    }
    
    private void swapNode(final RedBlackTreeNode<T> source, final RedBlackTreeNode<T> target) {
        target.setData(source.getData());
        this.map.put(target.getData().getKey(), target);
    }
    
    private void remove(RedBlackTreeNode<T> target) {
        if (target.getLeft() != null && target.getRight() != null) {
            RedBlackTreeNode<T> s;
            for (s = target.getLeft(); s.getRight() != null; s = s.getRight()) {}
            this.swapNode(s, target);
            target = s;
        }
        final RedBlackTreeNode<T> replacement = (target.getLeft() != null) ? target.getLeft() : target.getRight();
        if (replacement != null) {
            replacement.setParent(target.getParent());
            if (target.getParent() == null) {
                this.root = replacement;
            }
            else if (target == target.getParent().getLeft()) {
                target.getParent().setLeft(replacement);
            }
            else {
                target.getParent().setRight(replacement);
            }
            target.clearNode();
            if (target.getColor()) {
                this.fixAfterDeletion(replacement);
            }
        }
        else if (target.getParent() == null) {
            this.root = null;
        }
        else {
            if (target.getColor()) {
                this.fixAfterDeletion(target);
            }
            if (target.getParent() != null) {
                if (target == target.getParent().getLeft()) {
                    target.getParent().setLeft(null);
                }
                else if (target == target.getParent().getRight()) {
                    target.getParent().setRight(null);
                }
                target.setParent(null);
            }
        }
    }
    
    private void fixAfterInsertion(RedBlackTreeNode<T> x) {
        x.setColor(false);
        while (x != null && x != this.root && !x.getParent().getColor()) {
            if (this.getParentOfNode(x) == this.getLeftOfNode(this.getParentOfNode(this.getParentOfNode(x)))) {
                final RedBlackTreeNode<T> y = this.getRightOfNode(this.getParentOfNode(this.getParentOfNode(x)));
                if (!this.getColorOfNode(y)) {
                    this.setColorOfNode(this.getParentOfNode(x), true);
                    this.setColorOfNode(y, true);
                    this.setColorOfNode(this.getParentOfNode(this.getParentOfNode(x)), false);
                    x = this.getParentOfNode(this.getParentOfNode(x));
                }
                else {
                    if (x == this.getRightOfNode(this.getParentOfNode(x))) {
                        x = this.getParentOfNode(x);
                        this.rotateLeft(x);
                    }
                    this.setColorOfNode(this.getParentOfNode(x), true);
                    this.setColorOfNode(this.getParentOfNode(this.getParentOfNode(x)), false);
                    this.rotateRight(this.getParentOfNode(this.getParentOfNode(x)));
                }
            }
            else {
                final RedBlackTreeNode<T> y = this.getLeftOfNode(this.getParentOfNode(this.getParentOfNode(x)));
                if (!this.getColorOfNode(y)) {
                    this.setColorOfNode(this.getParentOfNode(x), true);
                    this.setColorOfNode(y, true);
                    this.setColorOfNode(this.getParentOfNode(this.getParentOfNode(x)), false);
                    x = this.getParentOfNode(this.getParentOfNode(x));
                }
                else {
                    if (x == this.getLeftOfNode(this.getParentOfNode(x))) {
                        x = this.getParentOfNode(x);
                        this.rotateRight(x);
                    }
                    this.setColorOfNode(this.getParentOfNode(x), true);
                    this.setColorOfNode(this.getParentOfNode(this.getParentOfNode(x)), false);
                    this.rotateLeft(this.getParentOfNode(this.getParentOfNode(x)));
                }
            }
        }
        this.root.setColor(true);
    }
    
    private void fixAfterDeletion(RedBlackTreeNode<T> x) {
        while (x != this.root && this.getColorOfNode(x)) {
            if (x == this.getLeftOfNode(this.getParentOfNode(x))) {
                RedBlackTreeNode<T> sib = this.getRightOfNode(this.getParentOfNode(x));
                if (!this.getColorOfNode(sib)) {
                    this.setColorOfNode(sib, true);
                    this.setColorOfNode(this.getParentOfNode(x), false);
                    this.rotateLeft(this.getParentOfNode(x));
                    sib = this.getRightOfNode(this.getParentOfNode(x));
                }
                if (this.getColorOfNode(this.getLeftOfNode(sib)) && this.getColorOfNode(this.getRightOfNode(sib))) {
                    this.setColorOfNode(sib, false);
                    x = this.getParentOfNode(x);
                }
                else {
                    if (this.getColorOfNode(this.getRightOfNode(sib))) {
                        this.setColorOfNode(this.getLeftOfNode(sib), true);
                        this.setColorOfNode(sib, false);
                        this.rotateRight(sib);
                        sib = this.getRightOfNode(this.getParentOfNode(x));
                    }
                    this.setColorOfNode(sib, this.getColorOfNode(this.getParentOfNode(x)));
                    this.setColorOfNode(this.getParentOfNode(x), true);
                    this.setColorOfNode(this.getRightOfNode(sib), true);
                    this.rotateLeft(this.getParentOfNode(x));
                    x = this.root;
                }
            }
            else {
                RedBlackTreeNode<T> sib = this.getLeftOfNode(this.getParentOfNode(x));
                if (!this.getColorOfNode(sib)) {
                    this.setColorOfNode(sib, true);
                    this.setColorOfNode(this.getParentOfNode(x), false);
                    this.rotateRight(this.getParentOfNode(x));
                    sib = this.getLeftOfNode(this.getParentOfNode(x));
                }
                if (this.getColorOfNode(this.getRightOfNode(sib)) && this.getColorOfNode(this.getLeftOfNode(sib))) {
                    this.setColorOfNode(sib, false);
                    x = this.getParentOfNode(x);
                }
                else {
                    if (this.getColorOfNode(this.getLeftOfNode(sib))) {
                        this.setColorOfNode(this.getRightOfNode(sib), true);
                        this.setColorOfNode(sib, false);
                        this.rotateLeft(sib);
                        sib = this.getLeftOfNode(this.getParentOfNode(x));
                    }
                    this.setColorOfNode(sib, this.getColorOfNode(this.getParentOfNode(x)));
                    this.setColorOfNode(this.getParentOfNode(x), true);
                    this.setColorOfNode(this.getLeftOfNode(sib), true);
                    this.rotateRight(this.getParentOfNode(x));
                    x = this.root;
                }
            }
        }
        this.setColorOfNode(x, true);
    }
    
    private void inIterator(final RedBlackTreeNode<T> node, final List<T> list, final boolean needClone) {
        if (node.getLeft() != null) {
            this.inIterator(node.getLeft(), list, needClone);
        }
        if (needClone) {
            list.add(node.getData().getClone());
        }
        else {
            list.add(node.getData());
        }
        if (node.getRight() != null) {
            this.inIterator(node.getRight(), list, needClone);
        }
    }
    
    private void operateItem(final RedBlackTreeNode<T> node, final IChartsItemOperator<T> operator) {
        if (node.getLeft() != null) {
            this.operateItem(node.getLeft(), operator);
        }
        operator.operate(node.getData());
        this.handleAfterSetSeq(node.getData().getSeq(), node.getData());
        if (node.getRight() != null) {
            this.operateItem(node.getRight(), operator);
        }
    }
    
    private int operateItem(final RedBlackTreeNode<T> node, final IChartsItemOperator<T> operator, int currNum, final int maxNum) {
        if (currNum >= maxNum) {
            return currNum;
        }
        if (node.getLeft() != null) {
            currNum = this.operateItem(node.getLeft(), operator, currNum, maxNum);
            if (currNum >= maxNum) {
                return currNum;
            }
        }
        operator.operate(node.getData());
        this.handleAfterSetSeq(node.getData().getSeq(), node.getData());
        if (++currNum >= maxNum) {
            return currNum;
        }
        if (node.getRight() != null) {
            currNum = this.operateItem(node.getRight(), operator, currNum, maxNum);
        }
        return currNum;
    }
    
    private boolean inIterator(final RedBlackTreeNode<T> node, final List<T> list, final int maxNum, final boolean needClone) {
        if (node.getLeft() != null && this.inIterator(node.getLeft(), list, maxNum, needClone)) {
            return true;
        }
        if (needClone) {
            list.add(node.getData().getClone());
        }
        else {
            list.add(node.getData());
        }
        return list.size() >= maxNum || (node.getRight() != null && this.inIterator(node.getRight(), list, maxNum, needClone));
    }
    
    private boolean inIterator(final RedBlackTreeNode<T> node, final List<T> list, final int minSeq, final int maxSeq, final boolean needClone) {
        if (node.getLeft() != null && this.inIterator(node.getLeft(), list, minSeq, maxSeq, needClone)) {
            return true;
        }
        if (node.getData().getSeq() >= minSeq && node.getData().getSeq() <= maxSeq) {
            if (needClone) {
                list.add(node.getData().getClone());
            }
            else {
                list.add(node.getData());
            }
        }
        return node.getData().getSeq() >= maxSeq || (node.getRight() != null && this.inIterator(node.getRight(), list, minSeq, maxSeq, needClone));
    }
    
    @Override
    public List<T> inIterator(final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn) {
        return this.inIterator(condition, meetFirstNotPassReturn, true);
    }
    
    @Override
    public List<T> inIterator(final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn, final boolean needClone) {
        if (this.root == null) {
            return new ArrayList<T>();
        }
        final List<T> list = new ArrayList<T>();
        this.inIterator(this.root, list, condition, meetFirstNotPassReturn, needClone);
        return list;
    }
    
    private boolean inIterator(final RedBlackTreeNode<T> node, final List<T> list, final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn, final boolean needClone) {
        if (node.getLeft() != null && this.inIterator(node.getLeft(), list, condition, meetFirstNotPassReturn, needClone)) {
            return true;
        }
        if (condition.isPass(node.getData())) {
            if (needClone) {
                list.add(node.getData().getClone());
            }
            else {
                list.add(node.getData());
            }
        }
        else if (meetFirstNotPassReturn) {
            return true;
        }
        return node.getRight() != null && this.inIterator(node.getRight(), list, condition, meetFirstNotPassReturn, needClone);
    }
    
    private boolean getColorOfNode(final RedBlackTreeNode<T> p) {
        return p == null || p.getColor();
    }
    
    private RedBlackTreeNode<T> getParentOfNode(final RedBlackTreeNode<T> p) {
        return (p == null) ? null : p.getParent();
    }
    
    private void setColorOfNode(final RedBlackTreeNode<T> p, final boolean color) {
        if (p != null) {
            p.setColor(color);
        }
    }
    
    private RedBlackTreeNode<T> getLeftOfNode(final RedBlackTreeNode<T> p) {
        return (p == null) ? null : p.getLeft();
    }
    
    private RedBlackTreeNode<T> getRightOfNode(final RedBlackTreeNode<T> p) {
        return (p == null) ? null : p.getRight();
    }
    
    private void rotateLeft(final RedBlackTreeNode<T> p) {
        if (p != null) {
            final RedBlackTreeNode<T> r = p.getRight();
            final RedBlackTreeNode<T> q = r.getLeft();
            p.setRight(q);
            if (q != null) {
                q.setParent(p);
            }
            r.setParent(p.getParent());
            if (p.getParent() == null) {
                this.root = r;
            }
            else if (p.getParent().getLeft() == p) {
                p.getParent().setLeft(r);
            }
            else {
                p.getParent().setRight(r);
            }
            r.setLeft(p);
            p.setParent(r);
        }
    }
    
    private void rotateRight(final RedBlackTreeNode<T> p) {
        if (p != null) {
            final RedBlackTreeNode<T> l = p.getLeft();
            final RedBlackTreeNode<T> q = l.getRight();
            p.setLeft(q);
            if (q != null) {
                q.setParent(p);
            }
            l.setParent(p.getParent());
            if (p.getParent() == null) {
                this.root = l;
            }
            else if (p.getParent().getRight() == p) {
                p.getParent().setRight(l);
            }
            else {
                p.getParent().setLeft(l);
            }
            l.setRight(p);
            p.setParent(l);
        }
    }
    
    private void operateFromNode(RedBlackTreeNode<T> node, final IChartsItemOperator<T> operator) {
        if (node == null) {
            return;
        }
        operator.operate(node.getData());
        this.handleAfterSetSeq(node.getData().getSeq(), node.getData());
        if (node.getRight() != null) {
            this.operateItem(node.getRight(), operator);
        }
        while (node.getParent() != null) {
            if (node.getParent().getLeft() == node) {
                this.operateFromNode(node.getParent(), operator);
                break;
            }
            node = node.getParent();
        }
    }
    
    private void operateFromNode(final RedBlackTreeNode<T> node, final IChartsItemOperator<T> operator, final int maxNum) {
        this.operateFromNode(node, operator, 0, maxNum);
    }
    
    private int operateFromNode(RedBlackTreeNode<T> node, final IChartsItemOperator<T> operator, int currNum, final int maxNum) {
        if (node == null) {
            return currNum;
        }
        if (currNum >= maxNum) {
            return currNum;
        }
        operator.operate(node.getData());
        this.handleAfterSetSeq(node.getData().getSeq(), node.getData());
        if (++currNum >= maxNum) {
            return currNum;
        }
        if (node.getRight() != null) {
            currNum = this.operateItem(node.getRight(), operator, currNum, maxNum);
            if (currNum >= maxNum) {
                return currNum;
            }
        }
        while (node.getParent() != null) {
            if (node.getParent().getLeft() == node) {
                return this.operateFromNode(node.getParent(), operator, currNum, maxNum);
            }
            node = node.getParent();
        }
        return currNum;
    }
    
    @Override
    public T getData(final int key, final int offset) {
        return this.getData(key, offset, true);
    }
    
    @Override
    public T getData(final int key, int offset, final boolean needClone) {
        RedBlackTreeNode<T> node = this.map.get(key);
        if (node == null) {
            return null;
        }
        if (offset > 0) {
            while (offset > 0) {
                node = this.getNextNode(node);
                if (node == null) {
                    return null;
                }
                --offset;
            }
        }
        if (offset < 0) {
            for (offset *= -1; offset > 0; --offset) {
                node = this.getPrevNode(node);
                if (node == null) {
                    return null;
                }
            }
        }
        if (needClone) {
            return node.getData().getClone();
        }
        return node.getData();
    }
    
    private RedBlackTreeNode<T> getNextNode(RedBlackTreeNode<T> node) {
        if (node.getRight() == null) {
            while (node.getParent() != null) {
                if (node.getParent().getLeft() == node) {
                    return node.getParent();
                }
                node = node.getParent();
            }
            return null;
        }
        for (node = node.getRight(); node.getLeft() != null; node = node.getLeft()) {}
        return node;
    }
    
    private RedBlackTreeNode<T> getPrevNode(RedBlackTreeNode<T> node) {
        if (node.getLeft() == null) {
            while (node.getParent() != null) {
                if (node.getParent().getRight() == node) {
                    return node.getParent();
                }
                node = node.getParent();
            }
            return null;
        }
        for (node = node.getLeft(); node.getRight() != null; node = node.getRight()) {}
        return node;
    }
}
