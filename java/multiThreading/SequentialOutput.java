public class SequentialOutput {
    private int counter = 1;
    private final int max = 10;
    private final int numberOfThreads = 3;

    public void printNumbers(int threadId) {
        synchronized (this) {
            while (counter <= max) {
                // Determine if it's this thread's turn
                // Thread 0 prints 3, 6, 9 (remainder 0)
                // Thread 1 prints 1, 4, 7, 10 (remainder 1)
                // Thread 2 prints 2, 5, 8 (remainder 2)
                while (counter <= max && counter % numberOfThreads != threadId % numberOfThreads) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (counter <= max) {
                    System.out.println("Thread " + threadId + ": " + counter);
                    counter++;
                    notifyAll(); // Wake up other threads to check the condition
                }
            }
        }
    }

    public static void main(String[] args) {
        SequentialOutput resource = new SequentialOutput();

        // Create 3 threads
        Thread t1 = new Thread(() -> resource.printNumbers(1), "T1");
        Thread t2 = new Thread(() -> resource.printNumbers(2), "T2");
        Thread t3 = new Thread(() -> resource.printNumbers(0), "T3");

        t1.start();
        t2.start();
        t3.start();
    }
}