package com.zyw.novelGame.collect.queue;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class RandomBlockingQueue<E> extends LinkedBlockingQueue<E> {
	/**
     * 定义长度
     */
    public RandomBlockingQueue(int capacity) {
        super(capacity);
    }
    
    /**
     * 随机获取其中一个值
     */
    public E randomGet() {
        int size = size();
        if (size == 0) {
            return null;
        }
        int count = new Random().nextInt(size);
        E element = null;
        Iterator<E> itr = iterator();
        while (itr.hasNext() && count-- >= 0) {
            element = itr.next();
        }
        return element;
    }
    
    /**
     * 借助于blockingQueue的容量大小保证，及offer方法的安全性来保证刷新队列
     */
    public void push(E element) {
        while (!offer(element)) {
            poll();
        }
    }

}
