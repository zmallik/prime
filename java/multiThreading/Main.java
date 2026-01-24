public class Main {
    public static void main(String[] args) {

        BoundedBlockingQueue<Integer> queue = new BoundedBlockingQueue<>(5);

        Thread producer = new Thread(new Producer(queue));
        Thread consumer = new Thread(new Consumer(queue));

        producer.start();
        consumer.start();
    }
}
