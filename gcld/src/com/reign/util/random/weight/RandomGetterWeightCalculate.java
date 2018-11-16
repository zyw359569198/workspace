package com.reign.util.random.weight;

import com.reign.util.random.*;
import com.reign.util.struct.*;
import java.util.*;

public class RandomGetterWeightCalculate<T> implements IRandomGetter<T>
{
    @Override
    public void getRandom(final List<T> source, final List<T> target, final int num) {
        for (int i = 0; i < num; ++i) {
            final int pos = RandomUtils.nextInt(source.size());
            target.add(source.get(pos));
            source.remove(pos);
        }
    }
    
    @Override
    public void getRandom(final List<T> source, final List<T> target, int num, final IWeightGetter<T> weightGetter) {
        target.clear();
        num = ((source.size() > num) ? num : source.size());
        final RandomGetterWeightCalculateComparator comparator = new RandomGetterWeightCalculateComparator();
        final BinaryHeap<RandomGetterWeightCalculateItem> binaryHeap = new BinaryHeap<RandomGetterWeightCalculateItem>(source.size(), comparator);
        for (final T t : source) {
            binaryHeap.enqueue(new RandomGetterWeightCalculateItem(t, weightGetter));
        }
        for (int i = 0; i < num; ++i) {
            target.add(binaryHeap.dequeueMin().getT());
        }
    }
    
    private class RandomGetterWeightCalculateComparator implements Comparator<RandomGetterWeightCalculateItem>
    {
        @Override
        public int compare(final RandomGetterWeightCalculateItem o1, final RandomGetterWeightCalculateItem o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return Double.compare(o2.getKey(), o1.getKey());
        }
    }
    
    private class RandomGetterWeightCalculateItem
    {
        private T t;
        private double key;
        
        public RandomGetterWeightCalculateItem(final T t, final IWeightGetter<T> weightGetter) {
            this.t = t;
            this.key = this.getValue(t, weightGetter);
        }
        
        public T getT() {
            return this.t;
        }
        
        public double getKey() {
            return this.key;
        }
        
        private double getValue(final T t, final IWeightGetter<T> weightGetter) {
            double value = 1.0;
            if (weightGetter != null) {
                value = weightGetter.getWeight(t);
            }
            else if (t instanceof IWeightable) {
                value = ((IWeightable)t).getWeight();
            }
            if (value > 0.0) {
                value = Math.pow(RandomUtils.nextDouble(), 1.0 / value);
            }
            else {
                value = 0.0;
            }
            return value;
        }
    }
}
