public class Consumer implements Runnable {

    private final BoundedBlockingQueue<Integer> queue;

    public Consumer(BoundedBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                int item = queue.take();
                System.out.println("Consumed: " + item);
                Thread.sleep(150);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
