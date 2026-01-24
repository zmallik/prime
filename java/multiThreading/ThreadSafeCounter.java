import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter implements Runnable {
     
     static AtomicInteger counter = new AtomicInteger(1); // a global counter

     public ThreadSafeCounter() {
     }

     static void incrementCounter() {
          System.out.println(Thread.currentThread().getName() + ": " + counter.getAndIncrement());
     }

     @Override
     public void run() {
          while(counter.get() < 10){
               incrementCounter();
          }
     }

     public static void main(String[] args) {
          ThreadSafeCounter te = new ThreadSafeCounter();
          Thread thread1 = new Thread(te);
          Thread thread2 = new Thread(te);

          thread1.start();
          thread2.start();          
     }
}
