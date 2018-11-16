package com.reign.framework.netty;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import org.jboss.netty.util.internal.*;
import org.jboss.netty.handler.execution.*;
import org.jboss.netty.channel.*;
import java.util.*;

public class CountAwareThreadPoolExecutor extends ThreadPoolExecutor
{
    private final ConcurrentMap<Channel, AtomicInteger> channelCounters;
    private final int countPerChannel;
    private final ConcurrentMap<Object, Executor> childExecutors;
    
    public CountAwareThreadPoolExecutor(final int corePoolSize, final int countPerChannel) {
        super(corePoolSize, corePoolSize, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        this.channelCounters = new ConcurrentIdentityHashMap();
        this.childExecutors = new ConcurrentIdentityWeakKeyHashMap();
        this.countPerChannel = countPerChannel;
    }
    
    @Override
    public void execute(final Runnable command) {
        if (command instanceof ChannelEventRunnable) {
            final ChannelEventRunnable event = (ChannelEventRunnable)command;
            final Channel channel = event.getEvent().getChannel();
            if (this.needRejected(channel)) {
                return;
            }
            this.getChildExecutor(event.getEvent()).execute(command);
        }
        else {
            this.doUnorderedExecute(command);
        }
    }
    
    protected void doUnorderedExecute(final Runnable task) {
        super.execute(task);
    }
    
    protected Object getChildExecutorKey(final ChannelEvent e) {
        return e.getChannel();
    }
    
    private Executor getChildExecutor(final ChannelEvent e) {
        final Object key = this.getChildExecutorKey(e);
        Executor executor = this.childExecutors.get(key);
        if (executor == null) {
            executor = new ChildExecutor();
            final Executor oldExecutor = this.childExecutors.putIfAbsent(key, executor);
            if (oldExecutor != null) {
                executor = oldExecutor;
            }
        }
        if (e instanceof ChannelStateEvent) {
            final Channel channel = e.getChannel();
            final ChannelStateEvent se = (ChannelStateEvent)e;
            if (se.getState() == ChannelState.OPEN && !channel.isOpen()) {
                this.childExecutors.remove(channel);
            }
        }
        return executor;
    }
    
    @Override
    protected void beforeExecute(final Thread t, final Runnable command) {
        super.beforeExecute(t, command);
        if (command instanceof ChannelEventRunnable) {
            final ChannelEventRunnable event = (ChannelEventRunnable)command;
            final Channel channel = event.getEvent().getChannel();
            final AtomicInteger counter = this.getChannelCounter(channel);
            if (counter != null) {
                counter.decrementAndGet();
            }
        }
    }
    
    private boolean needRejected(final Channel channel) {
        final AtomicInteger counter = this.getChannelCounter(channel);
        if (counter == null) {
            return false;
        }
        if (counter.get() > this.countPerChannel) {
            return true;
        }
        counter.incrementAndGet();
        return false;
    }
    
    private AtomicInteger getChannelCounter(final Channel channel) {
        AtomicInteger counter = this.channelCounters.get(channel);
        if (counter == null && channel.isOpen()) {
            counter = new AtomicInteger();
            final AtomicInteger oldCounter = this.channelCounters.putIfAbsent(channel, counter);
            if (oldCounter != null) {
                counter = oldCounter;
            }
        }
        if (!channel.isOpen()) {
            this.channelCounters.remove(channel);
        }
        return counter;
    }
    
    protected void onAfterExecute(final Runnable r, final Throwable t) {
        super.afterExecute(r, t);
    }
    
    private final class ChildExecutor implements Executor, Runnable
    {
        private final LinkedList<Runnable> tasks;
        
        ChildExecutor() {
            this.tasks = new LinkedList<Runnable>();
        }
        
        @Override
        public void execute(final Runnable command) {
            final boolean needsExecution;
            synchronized (this.tasks) {
                needsExecution = this.tasks.isEmpty();
                this.tasks.add(command);
            }
            // monitorexit(this.tasks)
            if (needsExecution) {
                CountAwareThreadPoolExecutor.this.doUnorderedExecute(this);
            }
        }
        
        @Override
        public void run() {
            final Thread thread = Thread.currentThread();
            while (true) {
                final Runnable task;
                synchronized (this.tasks) {
                    task = this.tasks.getFirst();
                }
                // monitorexit(this.tasks)
                boolean ran = false;
                CountAwareThreadPoolExecutor.this.beforeExecute(thread, task);
                try {
                    task.run();
                    ran = true;
                    CountAwareThreadPoolExecutor.this.onAfterExecute(task, null);
                }
                catch (RuntimeException e) {
                    if (!ran) {
                        CountAwareThreadPoolExecutor.this.onAfterExecute(task, e);
                    }
                    throw e;
                }
                finally {
                    synchronized (this.tasks) {
                        this.tasks.removeFirst();
                        if (this.tasks.isEmpty()) {
                            // monitorexit(this.tasks)
                            break;
                        }
                    }
                    // monitorexit(this.tasks)
                }
                synchronized (this.tasks) {
                    this.tasks.removeFirst();
                    if (!this.tasks.isEmpty()) {
                        // monitorexit(this.tasks)
                        continue;
                    }
                }
                // monitorexit(this.tasks)
                break;
            }
        }
    }
}
