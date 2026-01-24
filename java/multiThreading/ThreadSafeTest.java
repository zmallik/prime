import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

// 1. Atomic Approach
class AtomicCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    public void increment() { count.incrementAndGet(); }
    public int getValue() { return count.get(); }
}

// 2. Synchronized Approach
class SynchronizedCounter {
    private int count = 0;
    public synchronized void increment() {
        count++;
    }
    public synchronized int getValue() { return count; }
}

// 3. High-Contention Approach
class HighPerformanceCounter {
    private final LongAdder counter = new LongAdder();
    public void increment() { counter.increment(); }
    public long getValue() { return counter.sum(); }
}

public class ThreadSafeTest {
    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads = 1000;
        int incrementsPerThread = 1000;
        int expectedTotal = numberOfThreads * incrementsPerThread;

        // Initialize our counters
        AtomicCounter atomic = new AtomicCounter();
        SynchronizedCounter synced = new SynchronizedCounter();
        HighPerformanceCounter adder = new HighPerformanceCounter();

        // Create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(10);

        System.out.println("Starting increments...");

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    atomic.increment();
                    synced.increment();
                    adder.increment();
                }
            });
        }

        // Shut down and wait for threads to finish
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Display results
        System.out.println("--- Results ---");
        System.out.println("Expected Count:     " + expectedTotal);
        System.out.println("AtomicInteger:      " + atomic.getValue());
        System.out.println("Synchronized:       " + synced.getValue());
        System.out.println("LongAdder:          " + adder.getValue());
    }
}