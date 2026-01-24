import java.util.LinkedList;
import java.util.Queue;

public class BoundedBlockingQueue<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        // Wait while queue is full
        while (queue.size() == capacity) {
            wait();
        }

        queue.offer(item);
        // Wake up waiting consumers
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        // Wait while queue is empty
        while (queue.isEmpty()) {
            wait();
        }

        T item = queue.poll();
        // Wake up waiting producers
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}
