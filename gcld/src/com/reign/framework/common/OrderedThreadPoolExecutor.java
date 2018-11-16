package com.reign.framework.common;

import java.util.concurrent.*;
import java.util.*;

public class OrderedThreadPoolExecutor extends ThreadPoolExecutor
{
    private final ConcurrentMap<Object, Executor> childExecutors;
    
    public OrderedThreadPoolExecutor(final int threadNum) {
        super(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.childExecutors = new ConcurrentHashMap<Object, Executor>();
    }
    
    @Override
    public void execute(final Runnable command) {
        if (command instanceof OrderedRunnable) {
            final OrderedRunnable orderedRunnable = (OrderedRunnable)command;
            this.doOrderedExecute(orderedRunnable);
        }
        else {
            this.doUnorderedExecute(command);
        }
    }
    
    private void doOrderedExecute(final OrderedRunnable orderedRunnable) {
        Executor executor = this.childExecutors.get(orderedRunnable.getOrder());
        if (executor == null) {
            executor = new ChildExecutor();
            final Executor oldExecutor = this.childExecutors.putIfAbsent(orderedRunnable.getOrder(), executor);
            if (oldExecutor != null) {
                executor = oldExecutor;
            }
        }
        executor.execute(orderedRunnable);
    }
    
    private void doUnorderedExecute(final Runnable command) {
        super.execute(command);
    }
    
    public static void main(final String[] args) {
        final OrderedThreadPoolExecutor executor = new OrderedThreadPoolExecutor(5);
        for (int i = 0; i < 10; ++i) {
            final String str = String.valueOf(i / 2);
            final OrderTask task = new OrderTask(str);
            executor.execute(task);
        }
    }
    
    private final class ChildExecutor implements Executor, Runnable
    {
        private final LinkedList<OrderedRunnable> tasks;
        
        ChildExecutor() {
            this.tasks = new LinkedList<OrderedRunnable>();
        }
        
        @Override
        public void execute(final Runnable command) {
            final boolean needsExecution;
            synchronized (this.tasks) {
                needsExecution = this.tasks.isEmpty();
                this.tasks.add((OrderedRunnable)command);
            }
            // monitorexit(this.tasks)
            if (needsExecution) {
                OrderedThreadPoolExecutor.this.doUnorderedExecute(this);
            }
        }
        
        @Override
        public void run() {
            final Thread thread = Thread.currentThread();
            while (true) {
                final OrderedRunnable task;
                synchronized (this.tasks) {
                    task = this.tasks.getFirst();
                }
                // monitorexit(this.tasks)
                boolean ran = false;
                ThreadPoolExecutor.this.beforeExecute(thread, task);
                try {
                    task.run();
                    ran = true;
                    ThreadPoolExecutor.this.afterExecute(task, null);
                }
                catch (RuntimeException e) {
                    if (!ran) {
                        ThreadPoolExecutor.this.afterExecute(task, e);
                    }
                    throw e;
                }
                finally {
                    synchronized (this.tasks) {
                        this.tasks.removeFirst();
                        if (this.tasks.isEmpty()) {
                            OrderedThreadPoolExecutor.this.childExecutors.remove(task.getOrder());
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
                    OrderedThreadPoolExecutor.this.childExecutors.remove(task.getOrder());
                }
                // monitorexit(this.tasks)
                break;
            }
        }
    }
    
    public static class OrderTask implements OrderedRunnable
    {
        private String order;
        
        public OrderTask(final String order) {
            this.order = order;
        }
        
        @Override
        public void run() {
            System.out.println(String.valueOf(Thread.currentThread().getId()) + " execute me, my order is : " + this.order);
            try {
                Thread.currentThread();
                Thread.sleep(2000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public Object getOrder() {
            return this.order;
        }
    }
}
