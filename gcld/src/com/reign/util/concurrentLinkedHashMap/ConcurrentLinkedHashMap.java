package com.reign.util.concurrentLinkedHashMap;

import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public final class ConcurrentLinkedHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    static final long MAXIMUM_CAPACITY = 9223372034707292160L;
    static final int MAXIMUM_BUFFER_SIZE = 1024;
    static final int BUFFER_THRESHOLD = 16;
    static final int NUMBER_OF_BUFFERS;
    static final int BUFFER_MASK;
    static final int AMORTIZED_DRAIN_THRESHOLD;
    static final Queue<?> DISCARDING_QUEUE;
    final ConcurrentMap<K, Node<K, V>> data;
    final int concurrencyLevel;
    final LinkedDeque<Node<K, V>> evictionDeque;
    final AtomicLong weightedSize;
    volatile long capacity;
    volatile int nextOrder;
    int drainedOrder;
    final Task[] tasks;
    final Lock evictionLock;
    final Queue<Task>[] buffers;
    final AtomicIntegerArray bufferLengths;
    final AtomicReference<DrainStatus> drainStatus;
    final EntryWeigher<? super K, ? super V> weigher;
    final Queue<Node<K, V>> pendingNotifications;
    final EvictionListener<K, V> listener;
    transient Set<K> keySet;
    transient Collection<V> values;
    transient Set<Map.Entry<K, V>> entrySet;
    static final long serialVersionUID = 1L;
    
    static {
        DISCARDING_QUEUE = new DiscardingQueue();
        NUMBER_OF_BUFFERS = ceilingNextPowerOfTwo(Runtime.getRuntime().availableProcessors());
        AMORTIZED_DRAIN_THRESHOLD = (1 + ConcurrentLinkedHashMap.NUMBER_OF_BUFFERS) * 16;
        BUFFER_MASK = ConcurrentLinkedHashMap.NUMBER_OF_BUFFERS - 1;
    }
    
    static int ceilingNextPowerOfTwo(final int x) {
        return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
    }
    
    private ConcurrentLinkedHashMap(final Builder<K, V> builder) {
        this.concurrencyLevel = builder.concurrencyLevel;
        this.capacity = Math.min(builder.capacity, 9223372034707292160L);
        this.data = new ConcurrentHashMap<K, Node<K, V>>(builder.initialCapacity, 0.75f, this.concurrencyLevel);
        this.weigher = builder.weigher;
        this.nextOrder = Integer.MIN_VALUE;
        this.weightedSize = new AtomicLong();
        this.drainedOrder = Integer.MIN_VALUE;
        this.evictionLock = new ReentrantLock();
        this.evictionDeque = new LinkedDeque<Node<K, V>>();
        this.drainStatus = new AtomicReference<DrainStatus>(DrainStatus.IDLE);
        this.bufferLengths = new AtomicIntegerArray(ConcurrentLinkedHashMap.NUMBER_OF_BUFFERS);
        this.buffers = new Queue[ConcurrentLinkedHashMap.NUMBER_OF_BUFFERS];
        for (int i = 0; i < ConcurrentLinkedHashMap.NUMBER_OF_BUFFERS; ++i) {
            this.buffers[i] = new ConcurrentLinkedQueue<Task>();
        }
        this.tasks = new Task[ConcurrentLinkedHashMap.AMORTIZED_DRAIN_THRESHOLD];
        this.listener = builder.listener;
        this.pendingNotifications = (Queue<Node<K, V>>)((this.listener == DiscardingListener.INSTANCE) ? ConcurrentLinkedHashMap.DISCARDING_QUEUE : new ConcurrentLinkedQueue<Object>());
    }
    
    static void checkNotNull(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }
    
    static void checkArgument(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }
    
    static void checkState(final boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }
    
    public long capacity() {
        return this.capacity;
    }
    
    public void setCapacity(final long capacity) {
        checkArgument(capacity >= 0L);
        this.evictionLock.lock();
        try {
            this.capacity = Math.min(capacity, 9223372034707292160L);
            this.drainBuffers();
            this.evict();
        }
        finally {
            this.evictionLock.unlock();
        }
        this.evictionLock.unlock();
        this.notifyListener();
    }
    
    boolean hasOverflowed() {
        return this.weightedSize.get() > this.capacity;
    }
    
    void evict() {
        while (this.hasOverflowed()) {
            final Node<K, V> node = this.evictionDeque.poll();
            if (node == null) {
                return;
            }
            if (this.data.remove(node.key, node)) {
                this.pendingNotifications.add(node);
            }
            this.makeDead(node);
        }
    }
    
    void afterCompletion(final Task task) {
        final boolean delayable = this.schedule(task);
        final DrainStatus status = this.drainStatus.get();
        if (status.shouldDrainBuffers(delayable)) {
            this.tryToDrainBuffers();
        }
        this.notifyListener();
    }
    
    boolean schedule(final Task task) {
        final int index = bufferIndex();
        final int buffered = this.bufferLengths.incrementAndGet(index);
        if (task.isWrite()) {
            this.buffers[index].add(task);
            this.drainStatus.set(DrainStatus.REQUIRED);
            return false;
        }
        if (buffered <= 1024) {
            this.buffers[index].add(task);
            return buffered <= 16;
        }
        this.bufferLengths.decrementAndGet(index);
        return false;
    }
    
    static int bufferIndex() {
        return (int)Thread.currentThread().getId() & ConcurrentLinkedHashMap.BUFFER_MASK;
    }
    
    int nextOrdering() {
        return this.nextOrder++;
    }
    
    void tryToDrainBuffers() {
        if (this.evictionLock.tryLock()) {
            try {
                this.drainStatus.set(DrainStatus.PROCESSING);
                this.drainBuffers();
            }
            finally {
                this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
                this.evictionLock.unlock();
            }
            this.drainStatus.compareAndSet(DrainStatus.PROCESSING, DrainStatus.IDLE);
            this.evictionLock.unlock();
        }
    }
    
    void drainBuffers() {
        final int maxTaskIndex = this.moveTasksFromBuffers(this.tasks);
        this.updateDrainedOrder(this.tasks, maxTaskIndex);
        this.runTasks(this.tasks, maxTaskIndex);
    }
    
    int moveTasksFromBuffers(final Task[] tasks) {
        int maxTaskIndex = -1;
        for (int i = 0; i < this.buffers.length; ++i) {
            final int maxIndex = this.moveTasksFromBuffer(tasks, i);
            maxTaskIndex = Math.max(maxIndex, maxTaskIndex);
        }
        return maxTaskIndex;
    }
    
    int moveTasksFromBuffer(final Task[] tasks, final int bufferIndex) {
        final Queue<Task> buffer = this.buffers[bufferIndex];
        int removedFromBuffer = 0;
        int maxIndex = -1;
        Task task;
        while ((task = buffer.poll()) != null) {
            ++removedFromBuffer;
            final int index = task.getOrder() - this.drainedOrder;
            if (index < 0) {
                task.run();
            }
            else {
                if (index >= tasks.length) {
                    maxIndex = tasks.length - 1;
                    this.addTaskToChain(tasks, task, maxIndex);
                    break;
                }
                maxIndex = Math.max(index, maxIndex);
                this.addTaskToChain(tasks, task, index);
            }
        }
        this.bufferLengths.addAndGet(bufferIndex, -removedFromBuffer);
        return maxIndex;
    }
    
    void addTaskToChain(final Task[] tasks, final Task task, final int index) {
        task.setNext(tasks[index]);
        tasks[index] = task;
    }
    
    void runTasks(final Task[] tasks, final int maxTaskIndex) {
        for (int i = 0; i <= maxTaskIndex; ++i) {
            this.runTasksInChain(tasks[i]);
            tasks[i] = null;
        }
    }
    
    void runTasksInChain(Task task) {
        while (task != null) {
            final Task current = task;
            task = task.getNext();
            current.setNext(null);
            current.run();
        }
    }
    
    void updateDrainedOrder(final Task[] tasks, final int maxTaskIndex) {
        if (maxTaskIndex >= 0) {
            final Task task = tasks[maxTaskIndex];
            this.drainedOrder = task.getOrder() + 1;
        }
    }
    
    boolean tryToRetire(final Node<K, V> node, final WeightedValue<V> expect) {
        if (expect.isAlive()) {
            final WeightedValue<V> retired = new WeightedValue<V>(expect.value, -expect.weight);
            return node.compareAndSet(expect, retired);
        }
        return false;
    }
    
    void makeRetired(final Node<K, V> node) {
        WeightedValue<V> current;
        WeightedValue<V> retired;
        do {
            current = node.get();
            if (!current.isAlive()) {
                return;
            }
            retired = new WeightedValue<V>(current.value, -current.weight);
        } while (!node.compareAndSet(current, retired));
    }
    
    void makeDead(final Node<K, V> node) {
        WeightedValue<V> current;
        WeightedValue<V> dead;
        do {
            current = node.get();
            dead = new WeightedValue<V>(current.value, 0);
        } while (!node.compareAndSet(current, dead));
        this.weightedSize.lazySet(this.weightedSize.get() - Math.abs(current.weight));
    }
    
    void notifyListener() {
        Node<K, V> node;
        while ((node = this.pendingNotifications.poll()) != null) {
            this.listener.onEviction(node.key, node.getValue());
        }
    }
    
    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }
    
    @Override
    public int size() {
        return this.data.size();
    }
    
    public long weightedSize() {
        return Math.max(0L, this.weightedSize.get());
    }
    
    @Override
    public void clear() {
        this.evictionLock.lock();
        try {
            Node<K, V> node;
            while ((node = this.evictionDeque.poll()) != null) {
                this.data.remove(node.key, node);
                this.makeDead(node);
            }
            for (int i = 0; i < this.buffers.length; ++i) {
                final Queue<Task> buffer = this.buffers[i];
                int removed = 0;
                Task task;
                while ((task = buffer.poll()) != null) {
                    if (task.isWrite()) {
                        task.run();
                    }
                    ++removed;
                }
                this.bufferLengths.addAndGet(i, -removed);
            }
        }
        finally {
            this.evictionLock.unlock();
        }
        this.evictionLock.unlock();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.data.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        checkNotNull(value);
        for (final Node<K, V> node : this.data.values()) {
            if (node.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public V get(final Object key) {
        final Node<K, V> node = this.data.get(key);
        if (node == null) {
            return null;
        }
        this.afterCompletion(new ReadTask(node));
        return node.getValue();
    }
    
    public V getQuietly(final Object key) {
        final Node<K, V> node = this.data.get(key);
        return (node == null) ? null : node.getValue();
    }
    
    @Override
    public V put(final K key, final V value) {
        return this.put(key, value, false);
    }
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.put(key, value, true);
    }
    
    V put(final K key, final V value, final boolean onlyIfAbsent) {
        checkNotNull(key);
        checkNotNull(value);
        final int weight = this.weigher.weightOf((Object)key, (Object)value);
        final WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
        final Node<K, V> node = new Node<K, V>(key, weightedValue);
    Label_0045:
        while (true) {
            final Node<K, V> prior = this.data.putIfAbsent(node.key, node);
            if (prior == null) {
                this.afterCompletion(new AddTask(node, weight));
                return null;
            }
            if (onlyIfAbsent) {
                this.afterCompletion(new ReadTask(prior));
                return prior.getValue();
            }
            WeightedValue<V> oldWeightedValue;
            do {
                oldWeightedValue = prior.get();
                if (!oldWeightedValue.isAlive()) {
                    continue Label_0045;
                }
            } while (!prior.compareAndSet(oldWeightedValue, weightedValue));
            final int weightedDifference = weight - oldWeightedValue.weight;
            final Task task = (weightedDifference == 0) ? new ReadTask(prior) : new UpdateTask(prior, weightedDifference);
            this.afterCompletion(task);
            return oldWeightedValue.value;
        }
    }
    
    @Override
    public V remove(final Object key) {
        final Node<K, V> node = this.data.remove(key);
        if (node == null) {
            return null;
        }
        this.makeRetired(node);
        this.afterCompletion(new RemovalTask(node));
        return node.getValue();
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        final Node<K, V> node = this.data.get(key);
        if (node == null || value == null) {
            return false;
        }
        WeightedValue<V> weightedValue = node.get();
        while (weightedValue.contains(value)) {
            if (this.tryToRetire(node, weightedValue)) {
                if (this.data.remove(key, node)) {
                    this.afterCompletion(new RemovalTask(node));
                    return true;
                }
                break;
            }
            else {
                weightedValue = node.get();
                if (weightedValue.isAlive()) {
                    continue;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public V replace(final K key, final V value) {
        checkNotNull(key);
        checkNotNull(value);
        final int weight = this.weigher.weightOf((Object)key, (Object)value);
        final WeightedValue<V> weightedValue = new WeightedValue<V>(value, weight);
        final Node<K, V> node = this.data.get(key);
        if (node == null) {
            return null;
        }
        WeightedValue<V> oldWeightedValue;
        do {
            oldWeightedValue = node.get();
            if (!oldWeightedValue.isAlive()) {
                return null;
            }
        } while (!node.compareAndSet(oldWeightedValue, weightedValue));
        final int weightedDifference = weight - oldWeightedValue.weight;
        final Task task = (weightedDifference == 0) ? new ReadTask(node) : new UpdateTask(node, weightedDifference);
        this.afterCompletion(task);
        return oldWeightedValue.value;
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        checkNotNull(key);
        checkNotNull(oldValue);
        checkNotNull(newValue);
        final int weight = this.weigher.weightOf((Object)key, (Object)newValue);
        final WeightedValue<V> newWeightedValue = new WeightedValue<V>(newValue, weight);
        final Node<K, V> node = this.data.get(key);
        if (node == null) {
            return false;
        }
        WeightedValue<V> weightedValue;
        do {
            weightedValue = node.get();
            if (!weightedValue.isAlive() || !weightedValue.contains(oldValue)) {
                return false;
            }
        } while (!node.compareAndSet(weightedValue, newWeightedValue));
        final int weightedDifference = weight - weightedValue.weight;
        final Task task = (weightedDifference == 0) ? new ReadTask(node) : new UpdateTask(node, weightedDifference);
        this.afterCompletion(task);
        return true;
    }
    
    @Override
    public Set<K> keySet() {
        final Set<K> ks = this.keySet;
        return (ks == null) ? (this.keySet = new KeySet()) : ks;
    }
    
    public Set<K> ascendingKeySet() {
        return this.ascendingKeySetWithLimit(Integer.MAX_VALUE);
    }
    
    public Set<K> ascendingKeySetWithLimit(final int limit) {
        return this.orderedKeySet(true, limit);
    }
    
    public Set<K> descendingKeySet() {
        return this.descendingKeySetWithLimit(Integer.MAX_VALUE);
    }
    
    public Set<K> descendingKeySetWithLimit(final int limit) {
        return this.orderedKeySet(false, limit);
    }
    
    Set<K> orderedKeySet(final boolean ascending, final int limit) {
        checkArgument(limit >= 0);
        this.evictionLock.lock();
        try {
            this.drainBuffers();
            final int initialCapacity = (this.weigher == Weighers.entrySingleton()) ? Math.min(limit, (int)this.weightedSize()) : 16;
            final Set<K> keys = new LinkedHashSet<K>(initialCapacity);
            final Iterator<Node<K, V>> iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();
            while (iterator.hasNext() && limit > keys.size()) {
                keys.add(iterator.next().key);
            }
            return Collections.unmodifiableSet((Set<? extends K>)keys);
        }
        finally {
            this.evictionLock.unlock();
        }
    }
    
    @Override
    public Collection<V> values() {
        final Collection<V> vs = this.values;
        return (vs == null) ? (this.values = new Values()) : vs;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> es = this.entrySet;
        return (es == null) ? (this.entrySet = new EntrySet()) : es;
    }
    
    public Map<K, V> ascendingMap() {
        return this.ascendingMapWithLimit(Integer.MAX_VALUE);
    }
    
    public Map<K, V> ascendingMapWithLimit(final int limit) {
        return this.orderedMap(true, limit);
    }
    
    public Map<K, V> descendingMap() {
        return this.descendingMapWithLimit(Integer.MAX_VALUE);
    }
    
    public Map<K, V> descendingMapWithLimit(final int limit) {
        return this.orderedMap(false, limit);
    }
    
    Map<K, V> orderedMap(final boolean ascending, final int limit) {
        checkArgument(limit >= 0);
        this.evictionLock.lock();
        try {
            this.drainBuffers();
            final int initialCapacity = (this.weigher == Weighers.entrySingleton()) ? Math.min(limit, (int)this.weightedSize()) : 16;
            final Map<K, V> map = new LinkedHashMap<K, V>(initialCapacity);
            final Iterator<Node<K, V>> iterator = ascending ? this.evictionDeque.iterator() : this.evictionDeque.descendingIterator();
            while (iterator.hasNext() && limit > map.size()) {
                final Node<K, V> node = iterator.next();
                map.put(node.key, node.getValue());
            }
            return Collections.unmodifiableMap((Map<? extends K, ? extends V>)map);
        }
        finally {
            this.evictionLock.unlock();
        }
    }
    
    Object writeReplace() {
        return new SerializationProxy(this);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
    
    static final class Node<K, V> extends AtomicReference<WeightedValue<V>> implements Linked<Node<K, V>>
    {
        final K key;
        Node<K, V> prev;
        Node<K, V> next;
        
        Node(final K key, final WeightedValue<V> weightedValue) {
            super(weightedValue);
            this.key = key;
        }
        
        @Override
        public Node<K, V> getPrevious() {
            return this.prev;
        }
        
        @Override
        public void setPrevious(final Node<K, V> prev) {
            this.prev = prev;
        }
        
        @Override
        public Node<K, V> getNext() {
            return this.next;
        }
        
        @Override
        public void setNext(final Node<K, V> next) {
            this.next = next;
        }
        
        V getValue() {
            return this.get().value;
        }
    }
    
    enum DiscardingListener implements EvictionListener<Object, Object>
    {
        INSTANCE("INSTANCE", 0);
        
        private DiscardingListener(final String s, final int n) {
        }
        
        @Override
        public void onEviction(final Object key, final Object value) {
        }
    }
    
    enum DrainStatus
    {
        IDLE(0) {
            @Override
            boolean shouldDrainBuffers(final boolean delayable) {
                return !delayable;
            }
        }, 
        REQUIRED(1) {
            @Override
            boolean shouldDrainBuffers(final boolean delayable) {
                return true;
            }
        }, 
        PROCESSING(2) {
            @Override
            boolean shouldDrainBuffers(final boolean delayable) {
                return false;
            }
        };
        
        private DrainStatus(final String s, final int n) {
        }
        
        abstract boolean shouldDrainBuffers(final boolean p0);
    }
    
    final class EntryIterator implements Iterator<Map.Entry<K, V>>
    {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;
        
        EntryIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public Map.Entry<K, V> next() {
            this.current = this.iterator.next();
            return new WriteThroughEntry(this.current);
        }
        
        @Override
        public void remove() {
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current.key);
            this.current = null;
        }
    }
    
    final class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        final ConcurrentLinkedHashMap<K, V> map;
        
        EntrySet() {
            this.map = ConcurrentLinkedHashMap.this;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Node<K, V> node = this.map.data.get(entry.getKey());
            return node != null && node.getValue().equals(entry.getValue());
        }
        
        @Override
        public boolean add(final Map.Entry<K, V> entry) {
            return this.map.putIfAbsent(entry.getKey(), entry.getValue()) == null;
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            return this.map.remove(entry.getKey(), entry.getValue());
        }
    }
    
    class ReadTask extends AbstractTask
    {
        final Node<K, V> node;
        
        ReadTask(final Node<K, V> node) {
            this.node = node;
        }
        
        @Override
        public void run() {
            if (ConcurrentLinkedHashMap.this.evictionDeque.contains(this.node)) {
                ConcurrentLinkedHashMap.this.evictionDeque.moveToBack(this.node);
            }
        }
        
        @Override
        public boolean isWrite() {
            return false;
        }
    }
    
    final class AddTask extends AbstractTask
    {
        final Node<K, V> node;
        final int weight;
        
        AddTask(final Node<K, V> node, final int weight) {
            this.weight = weight;
            this.node = node;
        }
        
        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.weightedSize.lazySet(ConcurrentLinkedHashMap.this.weightedSize.get() + this.weight);
            if (this.node.get().isAlive()) {
                ConcurrentLinkedHashMap.this.evictionDeque.add(this.node);
                ConcurrentLinkedHashMap.this.evict();
            }
        }
        
        @Override
        public boolean isWrite() {
            return true;
        }
    }
    
    final class RemovalTask extends AbstractTask
    {
        final Node<K, V> node;
        
        RemovalTask(final Node<K, V> node) {
            this.node = node;
        }
        
        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.evictionDeque.remove(this.node);
            ConcurrentLinkedHashMap.this.makeDead(this.node);
        }
        
        @Override
        public boolean isWrite() {
            return true;
        }
    }
    
    final class UpdateTask extends ReadTask
    {
        final int weightDifference;
        
        public UpdateTask(final Node<K, V> node, final int weightDifference) {
            super(node);
            this.weightDifference = weightDifference;
        }
        
        @Override
        public void run() {
            ConcurrentLinkedHashMap.this.weightedSize.lazySet(ConcurrentLinkedHashMap.this.weightedSize.get() + this.weightDifference);
            super.run();
            ConcurrentLinkedHashMap.this.evict();
        }
        
        @Override
        public boolean isWrite() {
            return true;
        }
    }
    
    static final class WeightedValue<V>
    {
        final int weight;
        final V value;
        
        WeightedValue(final V value, final int weight) {
            this.weight = weight;
            this.value = value;
        }
        
        boolean contains(final Object o) {
            return o == this.value || this.value.equals(o);
        }
        
        boolean isAlive() {
            return this.weight > 0;
        }
        
        boolean isRetired() {
            return this.weight < 0;
        }
        
        boolean isDead() {
            return this.weight == 0;
        }
    }
    
    final class KeySet extends AbstractSet<K>
    {
        final ConcurrentLinkedHashMap<K, V> map;
        
        KeySet() {
            this.map = ConcurrentLinkedHashMap.this;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean contains(final Object obj) {
            return ConcurrentLinkedHashMap.this.containsKey(obj);
        }
        
        @Override
        public boolean remove(final Object obj) {
            return this.map.remove(obj) != null;
        }
        
        @Override
        public Object[] toArray() {
            return this.map.data.keySet().toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.map.data.keySet().toArray(array);
        }
    }
    
    final class KeyIterator implements Iterator<K>
    {
        final Iterator<K> iterator;
        K current;
        
        KeyIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.keySet().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public K next() {
            return this.current = this.iterator.next();
        }
        
        @Override
        public void remove() {
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current);
            this.current = null;
        }
    }
    
    final class Values extends AbstractCollection<V>
    {
        @Override
        public int size() {
            return ConcurrentLinkedHashMap.this.size();
        }
        
        @Override
        public void clear() {
            ConcurrentLinkedHashMap.this.clear();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            return ConcurrentLinkedHashMap.this.containsValue(o);
        }
    }
    
    final class ValueIterator implements Iterator<V>
    {
        final Iterator<Node<K, V>> iterator;
        Node<K, V> current;
        
        ValueIterator() {
            this.iterator = ConcurrentLinkedHashMap.this.data.values().iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public V next() {
            this.current = this.iterator.next();
            return this.current.getValue();
        }
        
        @Override
        public void remove() {
            ConcurrentLinkedHashMap.checkState(this.current != null);
            ConcurrentLinkedHashMap.this.remove(this.current.key);
            this.current = null;
        }
    }
    
    final class WriteThroughEntry extends SimpleEntry<K, V>
    {
        static final long serialVersionUID = 1L;
        
        WriteThroughEntry(final Node<K, V> node) {
            super(node.key, node.getValue());
        }
        
        @Override
        public V setValue(final V value) {
            ConcurrentLinkedHashMap.this.put(((SimpleEntry<K, V>)this).getKey(), value);
            return super.setValue(value);
        }
        
        Object writeReplace() {
            return new SimpleEntry(this);
        }
    }
    
    static final class BoundedEntryWeigher<K, V> implements EntryWeigher<K, V>, Serializable
    {
        static final long serialVersionUID = 1L;
        final EntryWeigher<? super K, ? super V> weigher;
        
        BoundedEntryWeigher(final EntryWeigher<? super K, ? super V> weigher) {
            ConcurrentLinkedHashMap.checkNotNull(weigher);
            this.weigher = weigher;
        }
        
        @Override
        public int weightOf(final K key, final V value) {
            final int weight = this.weigher.weightOf((Object)key, (Object)value);
            ConcurrentLinkedHashMap.checkArgument(weight >= 1);
            return weight;
        }
        
        Object writeReplace() {
            return this.weigher;
        }
    }
    
    static final class DiscardingQueue extends AbstractQueue<Object>
    {
        @Override
        public boolean add(final Object e) {
            return true;
        }
        
        @Override
        public boolean offer(final Object e) {
            return true;
        }
        
        @Override
        public Object poll() {
            return null;
        }
        
        @Override
        public Object peek() {
            return null;
        }
        
        @Override
        public int size() {
            return 0;
        }
        
        @Override
        public Iterator<Object> iterator() {
            return Collections.emptyList().iterator();
        }
    }
    
    abstract class AbstractTask implements Task
    {
        final int order;
        Task task;
        
        AbstractTask() {
            this.order = ConcurrentLinkedHashMap.this.nextOrdering();
        }
        
        @Override
        public int getOrder() {
            return this.order;
        }
        
        @Override
        public Task getNext() {
            return this.task;
        }
        
        @Override
        public void setNext(final Task task) {
            this.task = task;
        }
    }
    
    static final class SerializationProxy<K, V> implements Serializable
    {
        final EntryWeigher<? super K, ? super V> weigher;
        final EvictionListener<K, V> listener;
        final int concurrencyLevel;
        final Map<K, V> data;
        final long capacity;
        static final long serialVersionUID = 1L;
        
        SerializationProxy(final ConcurrentLinkedHashMap<K, V> map) {
            this.concurrencyLevel = map.concurrencyLevel;
            this.data = new HashMap<K, V>(map);
            this.capacity = map.capacity;
            this.listener = map.listener;
            this.weigher = map.weigher;
        }
        
        Object readResolve() {
            final ConcurrentLinkedHashMap<K, V> map = new Builder<K, V>().concurrencyLevel(this.concurrencyLevel).maximumWeightedCapacity(this.capacity).listener(this.listener).weigher(this.weigher).build();
            map.putAll((Map<?, ?>)this.data);
            return map;
        }
    }
    
    public static final class Builder<K, V>
    {
        static final int DEFAULT_CONCURRENCY_LEVEL = 16;
        static final int DEFAULT_INITIAL_CAPACITY = 16;
        EvictionListener<K, V> listener;
        EntryWeigher<? super K, ? super V> weigher;
        int concurrencyLevel;
        int initialCapacity;
        long capacity;
        
        public Builder() {
            this.capacity = -1L;
            this.weigher = Weighers.entrySingleton();
            this.initialCapacity = 16;
            this.concurrencyLevel = 16;
            this.listener = (EvictionListener<K, V>)DiscardingListener.INSTANCE;
        }
        
        public Builder<K, V> initialCapacity(final int initialCapacity) {
            ConcurrentLinkedHashMap.checkArgument(initialCapacity >= 0);
            this.initialCapacity = initialCapacity;
            return this;
        }
        
        public Builder<K, V> maximumWeightedCapacity(final long capacity) {
            ConcurrentLinkedHashMap.checkArgument(capacity >= 0L);
            this.capacity = capacity;
            return this;
        }
        
        public Builder<K, V> concurrencyLevel(final int concurrencyLevel) {
            ConcurrentLinkedHashMap.checkArgument(concurrencyLevel > 0);
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }
        
        public Builder<K, V> listener(final EvictionListener<K, V> listener) {
            ConcurrentLinkedHashMap.checkNotNull(listener);
            this.listener = listener;
            return this;
        }
        
        public Builder<K, V> weigher(final Weigher<? super V> weigher) {
            this.weigher = ((weigher == Weighers.singleton()) ? Weighers.entrySingleton() : new BoundedEntryWeigher<Object, Object>(Weighers.asEntryWeigher((Weigher<? super Object>)weigher)));
            return this;
        }
        
        public Builder<K, V> weigher(final EntryWeigher<? super K, ? super V> weigher) {
            this.weigher = ((weigher == Weighers.entrySingleton()) ? Weighers.entrySingleton() : new BoundedEntryWeigher<Object, Object>((EntryWeigher<? super Object, ? super Object>)weigher));
            return this;
        }
        
        public ConcurrentLinkedHashMap<K, V> build() {
            ConcurrentLinkedHashMap.checkState(this.capacity >= 0L);
            return new ConcurrentLinkedHashMap<K, V>(this, null);
        }
    }
    
    interface Task extends Runnable
    {
        int getOrder();
        
        boolean isWrite();
        
        Task getNext();
        
        void setNext(final Task p0);
    }
}
