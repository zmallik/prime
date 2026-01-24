import javax.swing.*;

public class ThreadSafeCounter1 {

    public static class TExp implements Runnable{
        public static  int counter = 0;
        private static TExp INSTANCE;

        @Override
        public void run() {
            inc();
        }

        public static TExp getInstance(){
            if(INSTANCE == null){
                INSTANCE = new TExp();
            }
            return INSTANCE;
        }

        private TExp(){}

        synchronized public void inc(){
            if(counter < 10){
                counter++;
                System.out.println(Thread.currentThread().getName() + ": " + counter);
            }

        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(TExp.getInstance());
        Thread thread2 = new Thread(TExp.getInstance());
    }

}
