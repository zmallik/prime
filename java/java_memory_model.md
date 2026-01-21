
The Java Memory Model (JMM) is a specification that defines how the Java Virtual Machine (JVM) interacts with computer memory (RAM). In a multi-threaded environment, it serves as the "rulebook" that determines when one thread's changes to a shared variable become visible to other threads.

The Happens-Before Relationship"Happens-before" is a formal guarantee within the JMM. action A happens-before action B, then the results of A are guaranteed to be visible to the thread performing B.
It is not necessarily about the physical time of execution, but about visibility and ordering. </br>
**Key rules include:**</br>
*Program Order Rule:* Each action in a single thread happens-before every action in that thread that comes later in the program. </br>
*Volatile Variable Rule:* A write to a volatile field happens-before every subsequent read of that same field.</br>
*Monitor Lock Rule:* An unlock on a monitor (synchronized block) happens-before every subsequent lock on that same monitor.</br>
*Transitivity:* If $A$ happens-before $B$, and $B$ happens-before $C$, then $A$ happens-before $C$. </br>

**Why volatile is not enough for count++**</br>
The volatile keyword only guarantees visibility (it ensures threads read the latest value from main memory) and ordering (it prevents instruction reordering). It does not guarantee atomicity.


The count++ operation is actually three distinct steps at the bytecode level:

*Read:* Load the current value of count from memory into a CPU register.

*Modify:* Increment the value in that register.

*Write:* Store the new value back from the register into memory.

The Race Condition Scenario:
Imagine count = 5 and two threads try to increment it simultaneously:

Thread A reads 5.

Thread B reads 5 (because Thread A hasn't written back yet).

Thread A increments its local value to 6 and writes it to memory.

Thread B increments its local value to 6 and writes it to memory.

Even though volatile ensured both threads saw the "latest" value at the moment they read it, they both performed the calculation based on the same starting point. One increment is "lost," and the final value is 6 instead of 7.

The Solution: AtomicInteger
To fix this, you should use java.util.concurrent.atomic.AtomicInteger. It uses a **Compare-And-Swap (CAS)** operation, which is a hardware-level atomic instruction. It checks if the value is still what the thread expects before writing the new value; if not, it retries the operation until it succeeds.
