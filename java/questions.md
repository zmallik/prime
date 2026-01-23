
### Core Java

**Difference between Runnable vs Callable**

<img width="849" height="424" alt="Screenshot 2026-01-23 at 11 49 43 AM" src="https://github.com/user-attachments/assets/5be5318a-828c-46ba-ba77-94bdd1837ead" />

Runnable: Cannot return result, Exception must be handled inside run(), Best for fire-and-forget tasks

```
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Task running");
    }
}

public class Test {
    public static void main(String[] args) {
        Thread t = new Thread(new MyRunnable());
        t.start();
    }
}

```
Callable: Can return a value, Can throw checked exceptions, Designed for tasks that produce a result
```
class MyCallable implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        return 10 + 20;
    }
}

public class Test {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new MyCallable());

        Integer result = future.get(); // blocks until result available
        System.out.println(result);

        executor.shutdown();
    }
}
```



Explain OOPS concepts with real-time examples Difference between abstract class vs interface </br>
Why String is immutable? </br>
How equals) and hashCode() work internally </br>
Difference between HashMap vs ConcurrentHashMap </br>
What is volatile keyword? </br>
Can we override a static method? </br>

### Java 8 & Functional Programming </br>
What are functional interfaces? </br>
Difference between map() vs flatMap() </br>
Use cases of Stream API </br>
Program to find 2nd highest number using streams </br>
Explain Optional and why it is used </br>

### Multithreading & Concurrency </br>
Difference between Runnable vs Callable </br>
How ExecutorService works </br>
What is deadlock and how to avoid it </br>
Difference between synchronized block vs method </br>
Basics of CompletableFuture </br>

### Spring / Spring Boot </br>
Explain IOC & Dependency Injection </br>
Difference between @Component, @Service, @Repository </br>
How Spring Boot auto-configuration works </br>
How @Transactional works internally </br>
Difference between PUT vs PATCH< /br>
Global exception handling using @ControllerAdvice </br>

### Hibernate / JPA </br>
Difference between LAZY vs EAGER fetching </br>
What is N+1 problem? </br>
Difference between save ), persist), saveAndFlush () </br>
Explain 1st level vs 2nd level cache </br>

### SQL </br>
Query to find 2nd highest salary </br>
Difference between INNER JOIN VS LEFT JOIN </br>
What are indexes and their pros/cons </br>
