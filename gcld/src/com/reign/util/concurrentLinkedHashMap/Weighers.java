package com.reign.util.concurrentLinkedHashMap;

import java.util.*;
import java.io.*;

public final class Weighers
{
    private Weighers() {
        throw new AssertionError();
    }
    
    public static <K, V> EntryWeigher<K, V> asEntryWeigher(final Weigher<? super V> weigher) {
        return (weigher == singleton()) ? entrySingleton() : new EntryWeigherView<K, V>(weigher);
    }
    
    public static <K, V> EntryWeigher<K, V> entrySingleton() {
        return (EntryWeigher<K, V>)SingletonEntryWeigher.INSTANCE;
    }
    
    public static <V> Weigher<V> singleton() {
        return (Weigher<V>)SingletonWeigher.INSTANCE;
    }
    
    public static Weigher<byte[]> byteArray() {
        return ByteArrayWeigher.INSTANCE;
    }
    
    public static <E> Weigher<? super Iterable<E>> iterable() {
        return IterableWeigher.INSTANCE;
    }
    
    public static <E> Weigher<? super Collection<E>> collection() {
        return CollectionWeigher.INSTANCE;
    }
    
    public static <E> Weigher<? super List<E>> list() {
        return ListWeigher.INSTANCE;
    }
    
    public static <E> Weigher<? super Set<E>> set() {
        return SetWeigher.INSTANCE;
    }
    
    public static <A, B> Weigher<? super Map<A, B>> map() {
        return MapWeigher.INSTANCE;
    }
    
    enum ByteArrayWeigher implements Weigher<byte[]>
    {
        INSTANCE("INSTANCE", 0);
        
        private ByteArrayWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final byte[] value) {
            return value.length;
        }
    }
    
    enum CollectionWeigher implements Weigher<Collection<?>>
    {
        INSTANCE("INSTANCE", 0);
        
        private CollectionWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final Collection<?> values) {
            return values.size();
        }
    }
    
    enum IterableWeigher implements Weigher<Iterable<?>>
    {
        INSTANCE("INSTANCE", 0);
        
        private IterableWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final Iterable<?> values) {
            if (values instanceof Collection) {
                return ((Collection)values).size();
            }
            int size = 0;
            final Iterator<?> i = values.iterator();
            while (i.hasNext()) {
                i.next();
                ++size;
            }
            return size;
        }
    }
    
    enum ListWeigher implements Weigher<List<?>>
    {
        INSTANCE("INSTANCE", 0);
        
        private ListWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final List<?> values) {
            return values.size();
        }
    }
    
    enum MapWeigher implements Weigher<Map<?, ?>>
    {
        INSTANCE("INSTANCE", 0);
        
        private MapWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final Map<?, ?> values) {
            return values.size();
        }
    }
    
    enum SetWeigher implements Weigher<Set<?>>
    {
        INSTANCE("INSTANCE", 0);
        
        private SetWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final Set<?> values) {
            return values.size();
        }
    }
    
    enum SingletonEntryWeigher implements EntryWeigher<Object, Object>
    {
        INSTANCE("INSTANCE", 0);
        
        private SingletonEntryWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final Object key, final Object value) {
            return 1;
        }
    }
    
    enum SingletonWeigher implements Weigher<Object>
    {
        INSTANCE("INSTANCE", 0);
        
        private SingletonWeigher(final String s, final int n) {
        }
        
        @Override
        public int weightOf(final Object value) {
            return 1;
        }
    }
    
    static final class EntryWeigherView<K, V> implements EntryWeigher<K, V>, Serializable
    {
        static final long serialVersionUID = 1L;
        final Weigher<? super V> weigher;
        
        EntryWeigherView(final Weigher<? super V> weigher) {
            ConcurrentLinkedHashMap.checkNotNull(weigher);
            this.weigher = weigher;
        }
        
        @Override
        public int weightOf(final K key, final V value) {
            return this.weigher.weightOf((Object)value);
        }
    }
}
