Rate Limiter Algorithms
A Rate Limiter controls how many requests a client can make to a service in a given time window. It protects APIs from abuse, DDoS attacks, and overload.
Why Rate Limiting?
🛡️ Prevent abuse / DDoS attacks
💰 Control infrastructure costs
⚖️ Fair usage across clients
🔒 Security (brute force protection)
1. Token Bucket Algorithm
Concept
A bucket holds tokens (max = capacity)
Tokens are added at a fixed rate
Each request consumes 1 token
If bucket is empty → request is rejected
Visual
plaintext

Copy code
Capacity = 5 tokens

Time 0:   [🪙][🪙][🪙][🪙][🪙]   ← Full bucket
Request:  [🪙][🪙][🪙][🪙][ ]    ← 1 token consumed
Request:  [🪙][🪙][🪙][ ][ ]    ← 1 token consumed
...
Refill:   [🪙][🪙][🪙][🪙][ ]   ← 1 token added per second
Code
java

Copy code
```
class TokenBucket {
    private final int capacity;
    private final double refillRatePerSecond;
    private double tokens;
    private long lastRefillTime;

    public TokenBucket(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;   // ✅ Allow
        }
        return false;      // ❌ Reject
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double elapsed = (now - lastRefillTime) / 1000.0;
        tokens = Math.min(capacity, tokens + elapsed * refillRatePerSecond);
        lastRefillTime = now;
    }
}
```
Pros & Cons
✅ Pros	❌ Cons
Allows burst traffic	Hard to tune capacity vs rate
Smooth average rate	Race conditions in distributed systems
Memory efficient	
Best For
APIs that allow short bursts (e.g., AWS API Gateway)
2. Leaky Bucket Algorithm
Concept
Requests enter a queue (bucket)
Requests are processed at a fixed constant rate (leak)
If queue is full → request is dropped
Smooths out bursty traffic into a steady stream
Visual
plaintext

Copy code
Incoming (bursty):  ▓▓▓▓▓▓▓▓▓ → [  Queue  ] → ▓ ▓ ▓ ▓  (steady output)
                                   (capacity)
                                   overflow → DROP ❌
Code
java

Copy code
class LeakyBucket {
    private final int capacity;
    private final long leakRateMs;    // process 1 request every N ms
    private int currentWater;
    private long lastLeakTime;

    public LeakyBucket(int capacity, long leakRateMs) {
        this.capacity = capacity;
        this.leakRateMs = leakRateMs;
        this.currentWater = 0;
        this.lastLeakTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        leak();
        if (currentWater < capacity) {
            currentWater++;
            return true;   // ✅ Allow (queued)
        }
        return false;      // ❌ Drop (overflow)
    }

    private void leak() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastLeakTime;
        int leaked = (int) (elapsed / leakRateMs);
        if (leaked > 0) {
            currentWater = Math.max(0, currentWater - leaked);
            lastLeakTime = now;
        }
    }
}
Pros & Cons
✅ Pros	❌ Cons
Smooth, constant output rate	Burst requests are delayed, not served fast
Prevents server overload	Queue can fill up and drop valid requests
Simple to implement	Not great for bursty-but-valid traffic
Best For
Network traffic shaping, payment processing (strict rate)
3. Fixed Window Counter
Concept
Divide time into fixed windows (e.g., every 60s)
Count requests per window per client
If count exceeds limit → reject
Counter resets at the start of each new window
Visual
plaintext

Copy code
Window: [0s ─────────── 60s] [60s ─────────── 120s]
Count:   1  2  3  4  5  ❌         1  2  3 ...
Limit = 5 requests per window
Code
java

Copy code
class FixedWindowCounter {
    private final int limit;
    private final long windowSizeMs;
    private int count;
    private long windowStart;

    public FixedWindowCounter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.count = 0;
        this.windowStart = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // Reset window if expired
        if (now - windowStart >= windowSizeMs) {
            count = 0;
            windowStart = now;
        }

        if (count < limit) {
            count++;
            return true;   // ✅ Allow
        }
        return false;      // ❌ Reject
    }
}
⚠️ Edge Case — Boundary Burst Problem
plaintext

Copy code
Limit = 5 per minute

Requests at 0:59 → 5 requests ✅ (end of window 1)
Requests at 1:01 → 5 requests ✅ (start of window 2)

= 10 requests in 2 seconds! 🚨 Double the limit
Pros & Cons
✅ Pros	❌ Cons
Simple & fast	Boundary burst problem
Low memory (1 counter)	Not smooth
Easy to understand	Can allow 2x traffic at window edges
Best For
Simple use cases where boundary bursts are acceptable
4. Sliding Window Log
Concept
Store a timestamp log of every request
On each request, remove timestamps older than window
If log size < limit → allow, else reject
Most accurate algorithm
Visual
plaintext

Copy code
Window = 60s, Limit = 3

t=10: log=[10]           size=1 ✅
t=20: log=[10,20]        size=2 ✅
t=30: log=[10,20,30]     size=3 ✅
t=40: log=[10,20,30,40]  size=4 ❌ reject
t=71: log=[20,30,40,71]  t=10 expired, size=4→3 ✅
Code
java

Copy code
class SlidingWindowLog {
    private final int limit;
    private final long windowSizeMs;
    private final Deque<Long> requestLog;   // timestamps

    public SlidingWindowLog(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.requestLog = new ArrayDeque<>();
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSizeMs;

        // Remove expired timestamps
        while (!requestLog.isEmpty() && requestLog.peekFirst() <= windowStart) {
            requestLog.pollFirst();
        }

        if (requestLog.size() < limit) {
            requestLog.addLast(now);
            return true;   // ✅ Allow
        }
        return false;      // ❌ Reject
    }
}
Pros & Cons
✅ Pros	❌ Cons
Most accurate	High memory (stores all timestamps)
No boundary burst issue	O(n) cleanup on each request
Precise control	Not scalable for high traffic
Best For
Low-traffic, high-accuracy scenarios (admin APIs)
5. Sliding Window Counter (Hybrid) ⭐
Concept
Combines Fixed Window + Sliding Window Log
Uses two windows: current + previous
Estimate current window count using weighted average:
plaintext

Copy code
count = prev_count × (1 - elapsed/window) + curr_count
Visual
plaintext

Copy code
Window = 60s, Limit = 10

Previous window [0–60s]:  8 requests
Current window  [60–120s]: 3 requests, 30s elapsed (50% through)

Estimated = 8 × (1 - 0.5) + 3 = 4 + 3 = 7 → ✅ Allow
Code
java

Copy code
class SlidingWindowCounter {
    private final int limit;
    private final long windowSizeMs;
    private long prevWindowCount;
    private long currWindowCount;
    private long currWindowStart;

    public SlidingWindowCounter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.currWindowStart = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        // Roll over to next window
        if (now - currWindowStart >= windowSizeMs) {
            prevWindowCount = currWindowCount;
            currWindowCount = 0;
            currWindowStart = now;
        }

        double elapsed = (double)(now - currWindowStart) / windowSizeMs;

        // Weighted estimate
        double estimated = prevWindowCount * (1 - elapsed) + currWindowCount;

        if (estimated < limit) {
            currWindowCount++;
            return true;   // ✅ Allow
        }
        return false;      // ❌ Reject
    }
}
Pros & Cons
✅ Pros	❌ Cons
Low memory (2 counters)	Slight approximation (not 100% exact)
No boundary burst issue	
Fast O(1)	
Best balance of accuracy + performance	
Best For
Production systems — used by Cloudflare, Redis, Nginx
6. Algorithm Comparison Table
Algorithm	Accuracy	Memory	Burst Handling	Complexity	Used By
Token Bucket	High	Low	✅ Allows bursts	O(1)	AWS, Stripe
Leaky Bucket	High	Medium	❌ Smooths bursts	O(1)	Networks, Nginx
Fixed Window	Low	Very Low	⚠️ Boundary burst	O(1)	Simple APIs
Sliding Window Log	Highest	High	✅ Accurate	O(n)	Admin APIs
Sliding Window Counter	High	Very Low	✅ Good estimate	O(1)	Cloudflare, Redis
7. Distributed Rate Limiting
In distributed systems, rate limiters must be centralized:
plaintext

Copy code
Client → [API Gateway] → [Redis Rate Limiter] → [Backend Service]

Redis commands:
  INCR  user:123:count
  EXPIRE user:123:count 60

Or with Lua script (atomic):
  local count = redis.call('INCR', key)
  if count == 1 then
    redis.call('EXPIRE', key, window)
  end
  return count <= limit
