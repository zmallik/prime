

A bounded blocking queue blocks producers when full and consumers when empty, implemented using synchronized blocks with wait/notifyAll or locks with conditions.

### ✅ Use while, not if
Handles spurious wakeups correctly. Replacing while with if can cause incorrect behavior because threads may wake up without the condition being true (spurious wakeups or wrong-thread notifications).
This can lead to:</br>
Consuming from an empty queue
Producing into a full queue
Data corruption or exceptions
Rare, hard-to-reproduce bugs

### ✅ notifyAll() instead of notify()
Avoids deadlock when:
Multiple producers & consumers exist
Waking the wrong type of thread

### ✅ Intrinsic lock (synchronized)
Ensures mutual exclusion
wait() releases lock atomically

###Why notifyAll() instead of notify()?</br>
notifyAll() is used instead of notify() to avoid deadlocks and missed signals when multiple condition types (producers and consumers) are waiting on the same monitor.

### What notify() actually does
Wakes one arbitrary thread waiting on the monitor
You don’t control which one
That thread may not be able to make progress

notify() can wake the wrong type of thread and cause deadlock when multiple conditions exist. notifyAll() ensures that at least one eligible thread proceeds, making the system correct. notify() is an optimization that’s unsafe without condition separation; notifyAll() is the correctness primitive.

### How would you support timeouts?
A) Using wait(timeout) (synchronized version)
```
public synchronized boolean offer(T item, long timeoutMillis)
        throws InterruptedException {

    long deadline = System.currentTimeMillis() + timeoutMillis;

    while (queue.size() == capacity) {
        long remaining = deadline - System.currentTimeMillis();
        if (remaining <= 0) {
            return false; // timeout
        }
        wait(remaining);
    }

    queue.offer(item);
    notifyAll();
    return true;
}

```
B) Using Condition.awaitNanos() (preferred, precise)
```
public T poll(long timeout, TimeUnit unit)
        throws InterruptedException {

    long nanos = unit.toNanos(timeout);
    lock.lock();
    try {
        while (queue.isEmpty()) {
            if (nanos <= 0) return null;
            nanos = notEmpty.awaitNanos(nanos);
        }
        T item = queue.poll();
        notFull.signal();
        return item;
    } finally {
        lock.unlock();
    }
}

```
### How does this differ from BlockingQueue?
BlockingQueue is a production-ready abstraction that provides correctness, performance optimizations, fairness options, and rich APIs.
<img width="846" height="494" alt="Screenshot 2025-12-19 at 4 18 22 AM" src="https://github.com/user-attachments/assets/ec7b97bf-de0c-4730-bfdb-5a1e25801b56" />

#### Key implementations
ArrayBlockingQueue, LinkedBlockingQueue, PriorityBlockingQueue, SynchronousQueue
```
BlockingQueue<Integer> q = new ArrayBlockingQueue<>(10, true);
```
- What is fairness?  
Threads are serviced in roughly FIFO order, preventing starvation.
Unfair (default behavior): Fast threads can repeatedly acquire the lock Some threads may starve
```
new ReentrantLock(); // unfair (default)
Lock lock = new ReentrantLock(true); // fair, Threads acquire the lock in arrival order. Producers/consumers are not starved
```
- Fairness avoids starvation by ordering waiting threads, but it reduces throughput, so most queues are unfair by default.
<img width="576" height="220" alt="Screenshot 2025-12-19 at 4 27 07 AM" src="https://github.com/user-attachments/assets/8987e4ab-3ec3-42f3-b781-f6c790d44b7c" />

### ArrayBlockingQueue vs LinkedBlockingQueue  
  ArrayBlockingQueue is array-backed, bounded, and optionally fair; LinkedBlockingQueue is node-based, optionally bounded, and higher-throughput butless predictable in memory usage.
  - ArrayBlockingQueue is Bounded, memory-efficient, optionally fair, but lower throughput due to a single lock
  - LinkedBlockingQueue is Higher throughput with dual locks but higher memory usage and unbounded by default
  <img width="825" height="448" alt="Screenshot 2025-12-19 at 4 29 00 AM" src="https://github.com/user-attachments/assets/8d113749-436c-4069-b0ab-cfe7cbd1fee7" />

- Locking model
ArrayBlockingQueue </br>
```
final ReentrantLock lock;
```
a) One lock for both put and take, b) More contention, c) Simpler, more predictable  

LinkedBlockingQueue  
```
final ReentrantLock putLock;
final ReentrantLock takeLock;
```
a) Separate locks. b) Producers and consumers can work in parallel. c) Higher throughput under load

- Fairness
ArrayBlockingQueue
```
new ArrayBlockingQueue<>(100, true); // fair
```
FIFO lock acquisition, Prevents starvation, Lower throughput

LinkedBlockingQueue
Always unfair, Higher throughput, Starvation theoretically possible (rare)

Example  
```
new ThreadPoolExecutor(
    core, max, timeout,
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1000)
);
```
Common choice because: Throughput matters, Queue absorbs bursts

ArrayBlockingQueue: Systems needing strict backpressure  
```
new ArrayBlockingQueue<>(100, true);
```
Enforces limits, Prevents request pile-up, Safer in Kubernetes

### SynchronousQueue
```
A zero-capacity queue — it never stores elements.
```
- “I use SynchronousQueue when I want no queuing, only direct handoff, enforcing immediate backpressure.”
- put() blocks until a take() happens
- Direct handoff between producer and consumer
- use When you want zero buffering: Strict backpressure, Low latency, Tasks must be handled immediately
- use When tasks are short-lived: CPU-bound work, Fast I/O
- ex: Dynamic thread scaling: Used by newCachedThreadPool()
- Real-world examples: Async request handoff, Event dispatch systems, Work stealing / actor-style execution

**backpressure** : Backpressure is when slow consumers force producers to slow down instead of accumulating work.
<img width="620" height="231" alt="Screenshot 2025-12-19 at 4 49 06 AM" src="https://github.com/user-attachments/assets/a0d1c0f8-a118-47c9-9065-37d04c1ba50b" />

Cached thread pool Uses SynchronousQueue, Threads grow up to demand, Risky if tasks are slow
```
Executors.newCachedThreadPool();
```


Fixed thread pool Uses unbounded queue, Thread count never grows beyond n
```
Executors.newFixedThreadPool(n);
```

```
“Queue choice controls whether the system scales by threads or by buffering.”
```

-  Why does Executors.newFixedThreadPool use LinkedBlockingQueue?
Design intent, Predictability over throughput., Fixed number of threads, Unlimited queue, Simple mental model
```
new ThreadPoolExecutor(
    n, n,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()
);
```

Why this is controversial

⚠️ Problems:

No backpressure, Tasks pile up, Risk of OOM, Latency explosion, 

✔ Benefit: Simple API, Avoids task rejection, Backward compatibility, Modern best practice, Avoid Executors.newFixedThreadPool in production.

Instead:
```
new ThreadPoolExecutor(
    n, n,
    0L, TimeUnit.MILLISECONDS,
    new ArrayBlockingQueue<>(1000),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

### summary

- SynchronousQueue enforces immediate backpressure via direct handoff
- Queue choice defines how backpressure is applied
- Thread pool sizing is tightly coupled with queue behavior
- newFixedThreadPool favors simplicity but risks unbounded memory growth

